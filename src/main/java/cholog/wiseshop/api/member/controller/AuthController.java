package cholog.wiseshop.api.member.controller;


import cholog.wiseshop.api.member.dto.request.SignInRequest;
import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.api.member.dto.response.MemberResponse;
import cholog.wiseshop.api.member.service.AuthService;
import cholog.wiseshop.common.auth.Auth;
import cholog.wiseshop.db.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> checkSession(
        @Auth Member member
    ) {
        var response = authService.getMember(member);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest signUpRequest) {
        authService.signUpMember(signUpRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> signIn(@RequestBody SignInRequest signInRequest,
        HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        authService.signInMember(signInRequest, session);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            authService.signOut(session, response);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
