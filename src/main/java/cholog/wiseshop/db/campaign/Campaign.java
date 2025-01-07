package cholog.wiseshop.db.campaign;

import cholog.wiseshop.db.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private int goalQuantity;

    private int soldQuantity;

    private CampaignState state;

    public Campaign(Product product, LocalDateTime startDate, LocalDateTime endDate, int goalQuantity) {
        this.product = product;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalQuantity = goalQuantity;
    }

    public Campaign() {
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public int getGoalQuantity() {
        return goalQuantity;
    }
}
