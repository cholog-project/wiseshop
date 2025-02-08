package cholog.wiseshop.api.member.service;

import cholog.wiseshop.api.member.dto.request.SignInRequest;
import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

	private static final String SESSION_KEY = "member";
	private final MemberRepository memberRepository;
	private final CampaignRepository campaignRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberService(
		MemberRepository memberRepository,
		CampaignRepository campaignRepository,
		PasswordEncoder passwordEncoder
	) {
		this.memberRepository = memberRepository;
		this.campaignRepository = campaignRepository;
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

	public void deleteMember(Member member) {
		List<Campaign> campaigns = campaignRepository.findCampaignByMemberId(member.getId());
		if (campaigns.stream().anyMatch(it -> it.getState().equals(CampaignState.IN_PROGRESS))) {
			throw new WiseShopException(WiseShopErrorCode.MEMBER_INPROGRESS_CAMPAIGN_EXIST);
		}
		memberRepository.deleteById(member.getId());
	}
}
