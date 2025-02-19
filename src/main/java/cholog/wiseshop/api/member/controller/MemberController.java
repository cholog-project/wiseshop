package cholog.wiseshop.api.member.controller;

import cholog.wiseshop.api.member.service.MemberService;
import cholog.wiseshop.common.auth.Auth;
import cholog.wiseshop.db.member.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember(
        @Auth Member member
    ) {
        memberService.deleteMember(member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
