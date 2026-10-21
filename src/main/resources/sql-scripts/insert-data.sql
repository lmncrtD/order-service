
INSERT INTO public.orders (
    order_id,
    user_id,
    created_at,
    status,
    item_id,
    total_amount
)
VALUES
    -- Заказ 1001
    (1001, 1, DATE '2026-10-15', 'COMPLETED',  1, 75000.00),
    (1001, 1, DATE '2026-10-15', 'COMPLETED',  2,  1500.00),
    (1001, 1, DATE '2026-10-15', 'COMPLETED',  3,  4500.00),

    -- Заказ 1002
    (1002, 2, DATE '2026-10-20', 'PROCESSING', 4, 22000.00),
    (1002, 2, DATE '2026-10-20', 'PROCESSING', 5,  6500.00),

    -- Заказ 1003
    (1003, 3, DATE '2026-10-21', 'CREATED',    2,  1500.00),
    (1003, 3, DATE '2026-10-21', 'CREATED',    3,  4500.00),
    (1003, 3, DATE '2026-10-21', 'CREATED',    4, 22000.00);