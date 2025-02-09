package cholog.wiseshop.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.api.member.service.MemberService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.exception.WiseShopException;
import cholog.wiseshop.fixture.MemberFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberServiceTest extends BaseTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Nested
    class 사용자가_회원가입을_수행한다 {

        @Test
        void 사용자가_회원가입을_정상적으로_수행한다() {
            // given
            SignUpRequest signUpRequest = new SignUpRequest(
                "최준호",
                "junho@test.com",
                "12345678"
            );

            // when
            memberService.signUpMember(signUpRequest);

            // then
            assertThat(memberRepository.findByEmail("junho@test.com")).isNotEmpty();
        }

        @Test
        void 이미_가입된_이메일로_가입을_시도하면_예외() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);

            SignUpRequest request = new SignUpRequest(
                "주노",
                member.getEmail(),
                "123123"
            );

            // when & then
            Assertions.assertThrows(WiseShopException.class,
                () -> memberService.signUpMember(request));
        }
    }
}
