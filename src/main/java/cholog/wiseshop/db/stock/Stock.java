package cholog.wiseshop.db.stock;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Stock {

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
}
