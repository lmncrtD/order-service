--liquibase formatted sql

--changeset Lmncrt:001-create-order-events runInTransaction:false
CREATE TABLE analytics.order_events
(
    event_id UUID,
    event_type String,
    occurred_at DateTime64(9, 'UTC'),
    order_id UUID,
    user_id Int64,
    created_at DateTime64(9, 'UTC'),
    status String,
    total_amount Decimal(12, 2)
)
    ENGINE = MergeTree
ORDER BY (order_id, occurred_at, event_id);