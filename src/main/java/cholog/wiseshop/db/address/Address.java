package cholog.wiseshop.db.address;

import cholog.wiseshop.api.address.dto.request.CreateAddressRequest;
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
import java.util.Objects;
import java.util.Optional;

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
        boolean isDefault,
        Member member
    ) {
        this.postalCode = postalCode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
        this.member = member;
    }

    public boolean isOwner(Member member) {
        return Objects.equals(getMember().orElse(Member.createEmpty()), member.getId());
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

    public Optional<Member> getMember() {
        return Optional.ofNullable(member);
    }

    public static Address from(Member member, CreateAddressRequest request) {
        return new Address(
            request.postalCode(),
            request.roadAddress(),
            request.detailAddress(),
            request.isDefault(),
            member
        );
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
