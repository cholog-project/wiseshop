package cholog.wiseshop.api.member.domain;

import java.util.List;

public record MemberModel(
        String name,
        String email,
        List<String> address,
        List<String> payments
) {

    public MemberModel rename(String name) {
        return new MemberModel(
                name,
                this.email,
                this.address,
                this.payments
        );
    }
}
