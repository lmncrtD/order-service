package lmncrt.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;


@Entity
@Table(name = "items")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_name", unique = true)
    private String itemName;

    @Column(name = "item_count", unique = true)
    private Integer itemCount;

    @Column(name = "item_amount", precision = 12, scale = 2)
    private BigDecimal itemAmount;
}
