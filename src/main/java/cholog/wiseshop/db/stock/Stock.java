package cholog.wiseshop.db.stock;

import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "STOCK")
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
            throw new WiseShopException(WiseShopErrorCode.STOCK_NOT_AVAILABLE);
        }
        this.totalQuantity = modifyQuantity;
    }

    public void reduceQuantity(int orderQuantity) {
        this.totalQuantity -= orderQuantity;
    }

    public boolean hasQuantity(int orderQuantity) {
        return totalQuantity >= orderQuantity;
    }
}
