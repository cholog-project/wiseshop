package cholog.wiseshop.api.address.dto;

public record CreateAddressRequest(
    int postalCode,
    String roadAddress,
    String detailAddress,
    boolean isDefault
) {

}
