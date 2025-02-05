package cholog.wiseshop.api.order.domain;

import cholog.wiseshop.api.member.domain.MemberModel;
import cholog.wiseshop.api.product.domain.ProductModel;

public record OrderModel(
    ProductModel product,
    String address,
    MemberModel customer,
    Long payAmount
) {

}
