-- Заказ 3: наушники × 3, мышь × 1, клавиатура × 2
WITH new_order AS (
    INSERT INTO public.orders (
                               user_id, created_at, status, total_amount
        )
        VALUES (1, DATE '2026-10-21', 'CREATED', NULL)
        RETURNING id
),
     new_items AS (
         INSERT INTO public.items (
                                   item_name, item_count, item_amount
             )
             VALUES
                 ('Наушники',   3, 6500.00),
                 ('Мышь',       1, 1500.00),
                 ('Клавиатура', 2, 4500.00)
             RETURNING id
     )
INSERT INTO public.link_orders_items (order_id, items_id)
SELECT o.id, i.id
FROM new_order o
         CROSS JOIN new_items i;