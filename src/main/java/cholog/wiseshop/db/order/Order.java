package cholog.wiseshop.db.order;

import cholog.wiseshop.db.BaseTimeEntity;
import cholog.wiseshop.db.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int count;

    public Order() {

    }

    public Order(Product product, int count) {
        this.product = product;
        this.count = count;
    }

    public void updateCount(int count) {
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getCount() {
        return count;
    }
}
