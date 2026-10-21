package lmncrt.orderservice.entity;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;


@Entity
@Table(name = "items")
public class ItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_count")
    private Integer itemCount;

    @Column(name = "item_amount", precision = 12, scale = 2)
    private BigDecimal itemAmount;

    public ItemsEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public BigDecimal getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public ItemsEntity(Long id, String itemName, Integer itemCount, BigDecimal itemAmount) {
        this.id = id;
        this.itemName = itemName;
        this.itemCount = itemCount;
        this.itemAmount = itemAmount;
    }
}
