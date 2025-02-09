package cholog.wiseshop.db.product;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.stock.Stock;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name = "PRODUCT")
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CAMPAIGN_ID")
    private Campaign campaign;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STOCK_ID")
    private Stock stock;

    public Product(String name,
        String description,
        Integer price,
        Stock stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public Product(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product(String name, String description, int price, Campaign campaign, Stock stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.campaign = campaign;
        this.stock = stock;
    }

    public Product() {

    }

    public void modifyProduct(String name, String description) {
        this.name = name;
        this.description = description;
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

    public int getPrice() {
        return price;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public Stock getStock() {
        return stock;
    }
}
