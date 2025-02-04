package cholog.wiseshop.db.product;

import cholog.wiseshop.api.member.domain.MemberModel;
import cholog.wiseshop.api.product.domain.ProductModel;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.stock.Stock;
import jakarta.persistence.*;

@Table(name = "PRODUCT")
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID")
    private Campaign campaign;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "STOCK_ID")
    private Stock stock;

    public Product(
            String name,
            String description,
            Integer price,
            Stock stock
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public Product(String name,
                   String description,
                   int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product() {

    }

    public void modifyProduct(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ProductModel toModel(MemberModel memberModel) {
        return new ProductModel(
                getId(),
                getName(),
                getDescription(),
                (long) getStock().getTotalQuantity(), // FIXME: Stock 제거 후 quantity 멤버변수로 변경하기
                getPrice(),
                memberModel
        );
    }

    public void addCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public void modifyPrice(int price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getPrice() {
        return price;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public Stock getStock() {
        return stock;
    }
}
