package cholog.wiseshop.db.order;

import cholog.wiseshop.db.BaseTimeEntity;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.product.Product;
import jakarta.persistence.*;

@Table(name = "`ORDER`")
@Entity
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", unique = false)
    private Product product;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private int count;

    public Order() {
    }

    public Order(
            Product product,
            int count,
            Member member
    ) {
        this.product = product;
        this.count = count;
        this.member = member;
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
