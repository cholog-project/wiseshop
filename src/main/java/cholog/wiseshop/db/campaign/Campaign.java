package cholog.wiseshop.db.campaign;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endDate;

    private Integer goalQuantity;

    private Integer soldQuantity;

    private CampaignState state;

    public Campaign(LocalDateTime startDate, LocalDateTime endDate, int goalQuantity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalQuantity = goalQuantity;
        this.state = CampaignState.WAITING;
    }

    public Campaign() {
    }

    public void updateState(CampaignState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Integer getGoalQuantity() {
        return goalQuantity;
    }

    public CampaignState getState() {
        return state;
    }
}
