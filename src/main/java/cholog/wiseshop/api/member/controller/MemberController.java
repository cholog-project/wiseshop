package cholog.wiseshop.api.member.controller;

import cholog.wiseshop.api.member.dto.request.SignInRequest;
import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.api.member.service.MemberService;
import cholog.wiseshop.common.auth.Auth;
import cholog.wiseshop.db.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest signUpRequest) {
        memberService.signUpMember(signUpRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> signIn(@RequestBody SignInRequest signInRequest,
        HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        memberService.signInMember(signInRequest, session);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(
        @Auth Member member
    ) {
        memberService.deleteMember(member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            memberService.signOut(session, response);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
