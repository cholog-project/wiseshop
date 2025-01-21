package cholog.wiseshop.api.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class MemberServiceTest {

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
        //given
        SignUpRequest signUpRequest = new SignUpRequest("name", NEW_EMAIL, PASSWORD);

        //when
        memberService.signUpMember(signUpRequest);

        //then
        assertThat(memberRepository.findByEmail(NEW_EMAIL)).isNotEmpty();
    }

    @Test
    @DisplayName("이미 등록된 사용자가 가입할 경우 예외를 검증한다.")
    void signUpExistMember() {
        //given
        SignUpRequest signUpRequest = new SignUpRequest("name", EXIST_EMAIL, PASSWORD);

        //then
        assertThatThrownBy(
            () -> memberService.signUpMember(signUpRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
