package lmncrt.orderservice.repository;

import lmncrt.orderservice.entity.ItemsEntity;
import lmncrt.orderservice.entity.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemsRepository extends JpaRepository<ItemsEntity, Long> {

    @Query(value = """
            SELECT o.*
            FROM public.items o
            """, nativeQuery = true)
    List<ItemsEntity> findAll();

    //ItemsEntity findItemsById(Long id);

}
