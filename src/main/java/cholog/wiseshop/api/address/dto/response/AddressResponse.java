package cholog.wiseshop.api.address.dto.response;

public record AddressResponse(
    Long id,
    int postalCode,
    String roadAddress,
    String detailAddress,
    boolean isDefault,
    Long ownerId
) {

}
