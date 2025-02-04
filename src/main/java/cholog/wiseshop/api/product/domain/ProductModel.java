package cholog.wiseshop.api.product.domain;

import cholog.wiseshop.api.member.domain.MemberModel;

public record ProductModel(
        String name,
        String description,
        Long quantity,
        Long price,
        MemberModel supplier
) {
}
