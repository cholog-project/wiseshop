package cholog.wiseshop.db.product;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Table(name = "product")
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    protected Product() {
    }

    public Product(
        String name,
        String description,
        int price,
        Campaign campaign,
        Stock stock,
        Member member
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.campaign = campaign;
        this.stock = stock;
        this.owner = member;
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static final class ProductBuilder {

        private String name;
        private String description;
        private int price;
        private Campaign campaign;
        private Stock stock;
        private Member owner;

        private ProductBuilder() {
        }

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder price(int price) {
            this.price = price;
            return this;
        }

        public ProductBuilder campaign(Campaign campaign) {
            this.campaign = campaign;
            return this;
        }

        public ProductBuilder stock(Stock stock) {
            this.stock = stock;
            return this;
        }

        public ProductBuilder owner(Member member) {
            this.owner = member;
            return this;
        }

        public Product build() {
            return new Product(name, description, price, campaign, stock, owner);
        }
    }

    public void modifyProduct(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public void modifyPriceAndStock(int price, int totalQuantity) {
        if (campaign.isInProgress()) {
            throw new WiseShopException(WiseShopErrorCode.CAMPAIGN_ALREADY_IN_PROGRESS);
        }
        if (totalQuantity <= campaign.getGoalQuantity()) {
            throw new WiseShopException(WiseShopErrorCode.INVALID_TOTAL_QUANTITY);
        }
        this.price = price;
        this.stock.modifyTotalQuantity(totalQuantity);
    }

    public boolean isOwner(Member member) {
        return Objects.equals(member.getId(), owner.getId());
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

    public int getPrice() {
        return price;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public Stock getStock() {
        return stock;
    }

    public Member getOwner() {
        return owner;
    }
}
