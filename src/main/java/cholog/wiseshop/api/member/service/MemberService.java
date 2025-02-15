package cholog.wiseshop.api.member.service;

import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final CampaignRepository campaignRepository;

    public MemberService(
        MemberRepository memberRepository,
        CampaignRepository campaignRepository
    ) {
        this.memberRepository = memberRepository;
        this.campaignRepository = campaignRepository;
    }

    public void deleteMember(Member member) {
        List<Campaign> campaigns = campaignRepository.findCampaignByMemberId(member.getId());
        if (campaigns.stream().anyMatch(it -> it.getState().equals(CampaignState.IN_PROGRESS))) {
            throw new WiseShopException(WiseShopErrorCode.MEMBER_INPROGRESS_CAMPAIGN_EXIST);
        }
        memberRepository.deleteById(member.getId());
    }
}
