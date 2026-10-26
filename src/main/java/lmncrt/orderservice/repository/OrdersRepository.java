package lmncrt.orderservice.repository;

import lmncrt.orderservice.entity.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {

    @Query(value = """
        SELECT o.*
        FROM public.orders o
        """,nativeQuery = true)
    List<OrdersEntity> findAllOrders();

    @Query(
            value = """
                    SELECT o.*
                    FROM public.orders o
                    WHERE o.user_id = :id
                    """,
            nativeQuery = true)
    List<OrdersEntity> findAllOrdersByUserId(Long id);

//    @Query(value = """
//        SELECT o.*
//        FROM public.orders o
//        WHERE o.id = :id
//    """, nativeQuery = true)
//    OrdersEntity findbById(Long id);

}
