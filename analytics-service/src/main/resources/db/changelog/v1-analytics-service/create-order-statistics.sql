--liquibase formatted sql

--changeset Lmncrt:001-create-orders-statistics runInTransaction:false
create TABLE analytics.order_statistics
(
    calculated_at DATETIME64(3, 'UTC'),
    count_orders UInt64,
    created_orders UInt64,
    completed_orders UInt64,
    cancelled_orders UInt64,
    total_amount_completed_orders DECIMAL(18, 2),
    average_amount_orders DECIMAL(18, 2)
)
    ENGINE = MergeTree
    ORDER BY calculated_at;
