INSERT INTO analytics.order_statistics(
                                       calculated_at,
                                       count_orders,
                                       created_orders,
                                       completed_orders,
                                       cancelled_orders,
                                       total_amount_completed_orders,
                                       average_amount_orders
)
select now() as calculated_at ,
       count() as count_orders,
       countIf(status = 'CREATED') as created_orders,
       countIf(status = 'COMPLETED') as completed_orders,
       countIf(status = 'CANCELLED') as cancelled_orders,
       sumIf(total_amount, status = 'COMPLETED') as total_amount_completed_orders,
       if(count_orders = 0, 0, avg(total_amount)) as average_amount_orders
from
    (select oe.order_id,
            oe.status,
            oe.occurred_at,
            oe.total_amount
     from order_events oe
     order by oe.order_id, oe.occurred_at desc
     LIMIT 1 BY oe.order_id
    );