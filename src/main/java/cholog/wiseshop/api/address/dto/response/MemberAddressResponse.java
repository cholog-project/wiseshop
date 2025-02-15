package cholog.wiseshop.api.address.dto.response;

import cholog.wiseshop.db.address.Address;

public record MemberAddressResponse(
    Long id,
    int postalCode,
    String roadAddress,
    String detailAddress,
    boolean isDefault
) {

    public MemberAddressResponse(Address address) {
        this(
            address.getId(),
            address.getPostalCode(),
            address.getRoadAddress(),
            address.getDetailAddress(),
            address.isDefault()
        );
    }
}
