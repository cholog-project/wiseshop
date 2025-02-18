package cholog.wiseshop.api.member.service;

import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.address.AddressRepository;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
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
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    public MemberService(
        MemberRepository memberRepository,
        CampaignRepository campaignRepository,
        ProductRepository productRepository, OrderRepository orderRepository,
        AddressRepository addressRepository) {
        this.memberRepository = memberRepository;
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
    }

    public void deleteMember(Member member) {
        Long memberId = member.getId();
        List<Campaign> campaigns = campaignRepository.findAllByMemberId(memberId);
        if (campaigns.stream().anyMatch(it -> it.getState().equals(CampaignState.IN_PROGRESS))) {
            throw new WiseShopException(WiseShopErrorCode.MEMBER_INPROGRESS_CAMPAIGN_EXIST);
        }
        setEmptyParent(memberId, campaigns);
        memberRepository.deleteById(memberId);
    }

    private void setEmptyParent(Long memberId, List<Campaign> campaigns) {
        List<Product> products = productRepository.findByOwnerId(memberId);
        List<Order> orders = orderRepository.findAllByMemberId(memberId);
        List<Address> addresses = addressRepository.findAllByMemberId(memberId);

        campaigns.forEach(campaign -> campaign.setMember(null));
        products.forEach(product -> product.setOwner(null));
        orders.forEach(order -> order.setMember(null));
        addresses.forEach(address -> address.setMember(null));
    }
}
