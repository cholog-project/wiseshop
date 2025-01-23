package cholog.wiseshop.api.member.service;

import cholog.wiseshop.api.member.dto.request.SignInRequest;
import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private static final String SESSION_KEY = "member";
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signUpMember(SignUpRequest signUpRequest) {
        if (memberRepository.findByEmail(signUpRequest.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        String encodePassword = passwordEncoder.encode(signUpRequest.password());
        Member member = new Member(signUpRequest.email(), signUpRequest.name(), encodePassword);
        memberRepository.save(member);
    }

    public void signInMember(SignInRequest signInRequest, HttpSession session) {
        Member member = memberRepository.findByEmail(signInRequest.email())
            .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 아이디 입니다."));
        boolean matches = passwordEncoder.matches(signInRequest.password(), member.getPassword());
        if (!matches) {
            throw new IllegalArgumentException("올바르지 않은 아이디 입니다.");
        }
        session.setAttribute(SESSION_KEY, member);
    }
}
