package cholog.wiseshop.db.order;

import cholog.wiseshop.db.BaseTimeEntity;
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

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public static final class OrderBuilder {

        private int count;
        private Product product;
        private Member member;
        private String address;

        private OrderBuilder() {
        }

        public OrderBuilder count(int count) {
            this.count = count;
            return this;
        }

        public OrderBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public OrderBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public OrderBuilder address(String address) {
            this.address = address;
            return this;
        }

        public Order build() {
            return new Order(
                count,
                product,
                member,
                address
            );
        }
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
