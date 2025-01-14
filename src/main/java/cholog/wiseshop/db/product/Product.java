package cholog.wiseshop.db.product;

import cholog.wiseshop.db.stock.Stock;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private int price;

    @OneToOne
    @JoinColumn(name = "STOCK_ID")
    @Cascade(value = CascadeType.ALL)
    private Stock stock;

    public Product(String name, String description, Integer price, Stock stock) {
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

    public Product() {

    }

    public void modifyProduct(String name, String description) {
        this.name = name;
        this.description = description;
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

    public Stock getStock() {
        return stock;
    }
}
