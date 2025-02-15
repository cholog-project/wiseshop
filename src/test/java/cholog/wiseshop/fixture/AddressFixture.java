package cholog.wiseshop.fixture;

import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.member.Member;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class AddressFixture {

    public static Address 집주소(Member member) {
        return new Address(
            06160,
            "서울특별시 강남구 삼성동 142-35",
            "13층",
            true,
            member
        );
    }
}
