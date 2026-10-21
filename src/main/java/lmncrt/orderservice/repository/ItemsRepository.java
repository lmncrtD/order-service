package lmncrt.orderservice.repository;

import lmncrt.orderservice.entity.ItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemsRepository extends JpaRepository<ItemsEntity, Long> {
}
