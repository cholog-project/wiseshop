package cholog.wiseshop.db.address;

import cholog.wiseshop.db.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "ADDRESS")
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int postalCode;

    @Column(name = "road_address", length = 200)
    private String roadAddress;

    @Column(name = "detail_address", length = 200)
    private String detailAddress;

    @Column(name = "is_default")
    private boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Address() {
    }

    public Address(
        int postalCode,
        String roadAddress,
        String detailAddress,
        boolean isDefault
    ) {
        this.postalCode = postalCode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
    }

    public Long getId() {
        return id;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
