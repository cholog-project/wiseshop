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

    private int totalQuantity;

    public Stock(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Stock() {
    }

    public Long getId() {
        return id;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void modifyTotalQuantity(int modifyQuantity) {
        if (modifyQuantity < MINIMUM_QUANTITY) {
            throw new IllegalArgumentException("재고 수량은 최소 1개 이상이어야 합니다.");
        }
        this.totalQuantity = modifyQuantity;
    }

    public void reduceQuantity(int orderQuantity) {
        this.totalQuantity -= orderQuantity;
    }

    public boolean hasQuantity(int orderQuantity) {
        if (totalQuantity < orderQuantity) {
            throw new IllegalArgumentException(
                    "주문 가능 수량을 초과하였습니다. 주문 가능한 수량은 " + totalQuantity + "개 입니다.");
        }
        return true;
    }
}
