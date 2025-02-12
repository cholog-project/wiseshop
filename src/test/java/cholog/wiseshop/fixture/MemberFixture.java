package cholog.wiseshop.fixture;

import cholog.wiseshop.db.member.Member;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class MemberFixture {

    public static Member 최준호() {
        return new Member(
            "junho@test.com",
            "최준호",
            "12341234"
        );
    }

    public static Member 김준수() {
        return new Member(
            "junesoo@test.com",
            "김준수",
            "12341234"
        );
    }
}
