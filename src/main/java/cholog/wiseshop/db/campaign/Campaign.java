package cholog.wiseshop.db.campaign;

import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "campaign")
@Entity
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private int goalQuantity;

    private int soldQuantity;

    @Enumerated(EnumType.STRING)
    private CampaignState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    protected Campaign() {
    }

    public Campaign(
        LocalDateTime startDate,
        LocalDateTime endDate,
        int goalQuantity,
        Member member,
        LocalDateTime now
    ) {
        validateStartDate(startDate, now);
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalQuantity = goalQuantity;
        this.state = CampaignState.WAITING;
        // TODO : 현재 시간과 비교하여 state를 유동적으로 변경할 수 있게 하면 어떨지..
        // ex) endDate < now 이면 생성될 수 없고, startDate <= now && now <= endDate 이면 IN_PROGRESS로 생성된다.
        this.member = member;
    }

    private void validateStartDate(LocalDateTime startDate, LocalDateTime now) {
        Duration duration = Duration.between(startDate, now);
        if (duration.toHours() > 24) {
            throw new WiseShopException(WiseShopErrorCode.CAMPAIGN_INVALID_START_DATE);
        }
    }

    public static CampaignBuilder builder() {
        return new CampaignBuilder();
    }

    public static final class CampaignBuilder {

        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private int goalQuantity;
        private int soldQuantity;
        private CampaignState state;
        private Member member;
        private LocalDateTime now;

        private CampaignBuilder() {
        }

        public CampaignBuilder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public CampaignBuilder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public CampaignBuilder goalQuantity(int goalQuantity) {
            this.goalQuantity = goalQuantity;
            return this;
        }

        public CampaignBuilder soldQuantity(int soldQuantity) {
            this.soldQuantity = soldQuantity;
            return this;
        }

        public CampaignBuilder state(CampaignState state) {
            this.state = state;
            return this;
        }

        public CampaignBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public CampaignBuilder now(LocalDateTime now) {
            this.now = Objects.requireNonNullElseGet(now, LocalDateTime::now);
            return this;
        }

        public Campaign build() {
            Campaign campaign = new Campaign(startDate, endDate, goalQuantity, member, now);
            campaign.state = this.state;
            campaign.soldQuantity = this.soldQuantity;
            return campaign;
        }
    }

    public void updateState(CampaignState state) {
        this.state = state;
    }

    public void increaseSoldQuantity(int orderQuantity) {
        soldQuantity += orderQuantity;
        if (goalQuantity - soldQuantity == 0) {
            this.state = CampaignState.SUCCESS;
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

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public CampaignState getState() {
        return state;
    }

    public Member getMember() {
        return member;
    }

    public boolean isInProgress() {
        return CampaignState.IN_PROGRESS.equals(state);
    }
}
