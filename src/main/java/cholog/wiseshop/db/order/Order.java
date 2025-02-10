package cholog.wiseshop.db.order;

import cholog.wiseshop.db.BaseTimeEntity;
import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "`order`")
@Entity
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String address;

    public Order() {
    }

    public Order(
        int count,
        Product product,
        Member member,
        String address
    ) {
        this.count = count;
        this.product = product;
        this.member = member;
        this.address = address;
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

    public Member getMember() {
        return member;
    }

    public String getAddress() {
        return address;
    }
}
