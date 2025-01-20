package cholog.wiseshop.api.member.service;

import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signUpMember(SignUpRequest signUpRequest) {
        String encodePassword = passwordEncoder.encode(signUpRequest.password());
        Member member = new Member(signUpRequest.email(), encodePassword);
        if (memberRepository.findByEmail(signUpRequest.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        memberRepository.save(member);
    }
}
