package cholog.wiseshop.db.campaign;

import cholog.wiseshop.api.campaign.domain.CampaignStatus;
import cholog.wiseshop.db.member.Member;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "CAMPAIGN")
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

    private int goalQuantity;

    private int soldQuantity;

    private CampaignStatus state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public Campaign(
            LocalDateTime startDate,
            LocalDateTime endDate,
            int goalQuantity,
            Member member
    ) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalQuantity = goalQuantity;
        this.state = CampaignStatus.WAITING;
        this.member = member;
    }

    public Campaign(
            LocalDateTime startDate,
            LocalDateTime endDate,
            int goalQuantity
    ) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalQuantity = goalQuantity;
        this.state = CampaignStatus.WAITING;
    }

    public Campaign() {
    }

    public void updateState(CampaignStatus state) {
        this.state = state;
    }

    public void increaseSoldQuantity(int orderQuantity) {
        soldQuantity += orderQuantity;
        if (soldQuantity - orderQuantity == 0) {
            this.state = CampaignStatus.SUCCESS;
        }
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

    public int getGoalQuantity() {
        return goalQuantity;
    }

    public CampaignStatus getState() {
        return state;
    }

    public Member getMember() {
        return member;
    }

    public boolean isInProgress() {
        return CampaignStatus.IN_PROGRESS.equals(state);
    }
}
