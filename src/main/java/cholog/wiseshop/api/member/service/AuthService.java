package cholog.wiseshop.api.member.service;

import cholog.wiseshop.api.member.dto.request.SignInRequest;
import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.api.member.dto.response.MemberResponse;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private static final String SESSION_KEY = "member";
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        MemberRepository memberRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signUpMember(SignUpRequest signUpRequest) {
        if (memberRepository.findByEmail(signUpRequest.email()).isPresent()) {
            throw new WiseShopException(WiseShopErrorCode.ALREADY_EXIST_MEMBER);
        }
        String encodePassword = passwordEncoder.encode(signUpRequest.password());
        Member member = new Member(signUpRequest.email(), signUpRequest.name(), encodePassword);
        memberRepository.save(member);
    }

    public void signInMember(SignInRequest signInRequest, HttpSession session) {
        Member member = memberRepository.findByEmail(signInRequest.email())
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.MEMBER_ID_NOT_FOUND));
        boolean matches = passwordEncoder.matches(signInRequest.password(), member.getPassword());
        if (!matches) {
            throw new WiseShopException(WiseShopErrorCode.MEMBER_ID_NOT_FOUND);
        }
        session.setAttribute(SESSION_KEY, member);
    }

    public void signOut(HttpSession session, HttpServletResponse response) {
        session.invalidate();

        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setMaxAge(0);     // 쿠키 즉시 만료
        sessionCookie.setPath("/");     // 쿠키의 경로 설정
        response.addCookie(sessionCookie);
    }

    public MemberResponse getMember(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getEmail(),
            member.getName()
        );
    }
}
