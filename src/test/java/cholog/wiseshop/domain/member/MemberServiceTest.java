package cholog.wiseshop.domain.member;

import cholog.wiseshop.api.member.dto.request.SignInRequest;
import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.api.member.service.MemberService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceTest extends BaseTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String EXIST_EMAIL = "exist_email";
    private static final String NEW_EMAIL = "new_email";
    private static final String PASSWORD = "password";

    @BeforeEach
    void setUp() {
        memberRepository.deleteAllInBatch();
        memberRepository.save(new Member(EXIST_EMAIL, "name", passwordEncoder.encode(PASSWORD)));
    }

    @Test
    @DisplayName("새로운 사용자가 가입하는 경우 정상 동작을 검증한다.")
    void signUpNewMember() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest("name", NEW_EMAIL, PASSWORD);

        // when
        memberService.signUpMember(signUpRequest);

        // then
        assertThat(memberRepository.findByEmail(NEW_EMAIL)).isNotEmpty();
    }

    @Test
    @DisplayName("이미 등록된 사용자가 가입할 경우 예외를 검증한다.")
    void signUpExistMember() {
        //given
        SignUpRequest signUpRequest = new SignUpRequest("name", EXIST_EMAIL, PASSWORD);

        // then
        assertThatThrownBy(
                () -> memberService.signUpMember(signUpRequest))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.ALREADY_EXIST_MEMBER.getMessage());
    }


    @Test
    @DisplayName("로그인의 정상 동작을 검증한다.")
    void signInMember() {
        // given
        MockHttpSession session = new MockHttpSession();
        SignInRequest signInRequest = new SignInRequest(EXIST_EMAIL, PASSWORD);

        // when
        memberService.signInMember(signInRequest, session);

        // then
        assertThat(session.getAttribute("member")).isNotNull();
    }

    @Test
    @DisplayName("가입되지 않은 사용자가 로그인 할 때 예외를 검증한다.")
    void notExistMemberSignIn() {
        // given
        MockHttpSession session = new MockHttpSession();
        SignInRequest signInRequest = new SignInRequest(NEW_EMAIL, PASSWORD);

        // then
        assertThatThrownBy(() -> memberService.signInMember(signInRequest, session))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.MEMBER_ID_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 할 때 예외를 검증한다")
    void wrongPasswordSignIn() {
        // given
        MockHttpSession session = new MockHttpSession();
        SignInRequest signInRequest = new SignInRequest(NEW_EMAIL, "boyeZZANG");

        // then
        assertThatThrownBy(() -> memberService.signInMember(signInRequest, session))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.MEMBER_ID_NOT_FOUND.getMessage());
    }
}
