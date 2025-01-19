package cholog.wiseshop.db.stock;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Stock {

    private static final Integer MINIMUM_QUANTITY = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalQuantity;

    public Stock(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Stock() {
    }

    public Long getId() {
        return id;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void modifyTotalQuantity(Integer modifyQuantity) {
        if (modifyQuantity < MINIMUM_QUANTITY) {
            throw new IllegalArgumentException("재고 수량은 최소 1개 이상이어야 합니다.");
        }
        this.totalQuantity = modifyQuantity;
    }
}
