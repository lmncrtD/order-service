--liquibase formatted sql

--changeset Lmncrt:001-create-tables dbms:postgresql splitStatements:false runInTransaction:true
DO $seed$
DECLARE
    demo_order RECORD;
    new_order_id BIGINT;
    order_total NUMERIC(12, 2);
BEGIN
    -- Не объединяем старые дубли автоматически: у них могут различаться данные.
    IF EXISTS (
        SELECT 1
        FROM public.items
        GROUP BY item_name
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'В items уже есть повторяющиеся item_name. Сначала объедините дубли с сохранением связей заказов.';
    END IF;

    CREATE UNIQUE INDEX IF NOT EXISTS ux_items_item_name
        ON public.items (item_name);

    INSERT INTO public.items (item_name, item_count, item_amount)
    VALUES
        ('Ноутбук',         1, 75000.00),
        ('Мышь',            2,  1500.00),
        ('Клавиатура',      1,  4500.00),
        ('Монитор',         2, 22000.00),
        ('Наушники',        1,  6500.00),
        ('Веб-камера',      1,  4200.00),
        ('Микрофон',        1,  7800.00),
        ('Колонки',         2,  3200.00),
        ('SSD',             2,  8900.00),
        ('Внешний HDD',     1,  5400.00),
        ('USB-хаб',         3,  1800.00),
        ('Роутер',          1,  6200.00),
        ('Принтер',         1, 18500.00),
        ('Планшет',         1, 32000.00),
        ('Умные часы',      1, 14500.00)
    ON CONFLICT (item_name) DO NOTHING;

    -- Формат строки: номер примера, клиент, дата, статус, названия товаров.
    -- Клиент 1: пять заказов; клиент 2: четыре; клиенты 3 и 4: по три;
    -- клиент 5: два; клиенты 6, 7 и 8: по одному.
    FOR demo_order IN
        SELECT *
        FROM (VALUES
            ( 1, 1, DATE '2026-09-01', 'COMPLETED',  ARRAY['Ноутбук', 'Мышь', 'Клавиатура']),
            ( 2, 1, DATE '2026-09-12', 'COMPLETED',  ARRAY['Монитор']),
            ( 3, 1, DATE '2026-10-01', 'COMPLETED', ARRAY['Веб-камера', 'Микрофон', 'Наушники']),
            ( 4, 1, DATE '2026-10-15', 'CREATED',    ARRAY['USB-хаб', 'SSD']),
            ( 5, 1, DATE '2026-10-22', 'CREATED',    ARRAY['Роутер']),
            ( 6, 2, DATE '2026-09-03', 'COMPLETED',  ARRAY['Принтер', 'Внешний HDD']),
            ( 7, 2, DATE '2026-09-22', 'COMPLETED',  ARRAY['Ноутбук', 'Монитор', 'Клавиатура', 'Мышь', 'USB-хаб']),
            ( 8, 2, DATE '2026-10-10', 'COMPLETED', ARRAY['Наушники', 'Колонки']),
            ( 9, 2, DATE '2026-10-21', 'CREATED',    ARRAY['Планшет', 'Умные часы']),
            (10, 3, DATE '2026-09-05', 'COMPLETED',  ARRAY['Мышь']),
            (11, 3, DATE '2026-10-05', 'COMPLETED', ARRAY['SSD', 'Внешний HDD', 'USB-хаб']),
            (12, 3, DATE '2026-10-22', 'CREATED',    ARRAY['Веб-камера', 'Микрофон']),
            (13, 4, DATE '2026-09-10', 'COMPLETED',  ARRAY['Роутер']),
            (14, 4, DATE '2026-10-07', 'CREATED', ARRAY['Принтер', 'USB-хаб']),
            (15, 4, DATE '2026-10-20', 'CREATED',    ARRAY['Ноутбук', 'Наушники']),
            (16, 5, DATE '2026-09-15', 'COMPLETED',  ARRAY['Планшет']),
            (17, 5, DATE '2026-10-18', 'COMPLETED', ARRAY['Планшет', 'Умные часы', 'Наушники']),
            (18, 6, DATE '2026-10-11', 'COMPLETED',  ARRAY['Клавиатура', 'Мышь', 'Колонки', 'Монитор']),
            (19, 7, DATE '2026-10-21', 'CREATED',    ARRAY['Умные часы']),
            (20, 8, DATE '2026-10-22', 'CREATED',    ARRAY['Ноутбук', 'Монитор', 'Веб-камера', 'Микрофон', 'Наушники'])
        ) AS s(example_number, user_id, created_at, status, item_names)
        ORDER BY example_number
    LOOP
        -- Проверяем, что каждая позиция найдена ровно один раз.
        IF (
            SELECT COUNT(*)
            FROM public.items i
            WHERE i.item_name = ANY(demo_order.item_names)
        ) <> CARDINALITY(demo_order.item_names) THEN
            RAISE EXCEPTION 'Не найден полный набор товаров для примера %', demo_order.example_number;
        END IF;

        IF EXISTS (
            SELECT 1
            FROM public.items i
            WHERE i.item_name = ANY(demo_order.item_names)
              AND (i.item_count IS NULL OR i.item_count <= 0
                   OR i.item_amount IS NULL OR i.item_amount < 0)
        ) THEN
            RAISE EXCEPTION 'Некорректные цена или количество для примера %', demo_order.example_number;
        END IF;

        -- Считаем сумму из фактических значений в items.
        SELECT SUM(i.item_amount * i.item_count)
        INTO order_total
        FROM public.items i
        WHERE i.item_name = ANY(demo_order.item_names);

        INSERT INTO public.orders (user_id, created_at, status, total_amount)
        VALUES (demo_order.user_id, demo_order.created_at, demo_order.status, order_total)
        RETURNING id INTO new_order_id;

        INSERT INTO public.link_orders_items (order_id, items_id)
        SELECT new_order_id, i.id
        FROM public.items i
        WHERE i.item_name = ANY(demo_order.item_names);
    END LOOP;
END;
$seed$;
