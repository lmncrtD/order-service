package lmncrt.orderservice.repository;

import lmncrt.orderservice.entity.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {

    @Query(value = """
        SELECT o.*
        FROM public.orders o
        ORDER BY o.order_id, o.id
        """,nativeQuery = true)
    List<OrdersEntity> findAllOrders();

}
