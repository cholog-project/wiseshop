package cholog.wiseshop.api.address.dto.request;

public record CreateAddressRequest(
    int postalCode,
    String roadAddress,
    String detailAddress,
    boolean isDefault
) {

}
