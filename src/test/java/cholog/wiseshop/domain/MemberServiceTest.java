package cholog.wiseshop.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.member.dto.request.SignUpRequest;
import cholog.wiseshop.api.member.service.AuthService;
import cholog.wiseshop.api.member.service.MemberService;
import cholog.wiseshop.common.BaseTest;
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
import cholog.wiseshop.fixture.AddressFixture;
import cholog.wiseshop.fixture.CampaignFixture;
import cholog.wiseshop.fixture.MemberFixture;
import cholog.wiseshop.fixture.OrderFixture;
import cholog.wiseshop.fixture.ProductFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberServiceTest extends BaseTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberService memberService;

    @Nested
    class 사용자가_회원가입을_수행한다 {

        @Test
        void 사용자가_회원가입을_정상적으로_수행한다() {
            // given
            SignUpRequest signUpRequest = new SignUpRequest(
                "최준호",
                "junho@test.com",
                "12345678"
            );

            // when
            authService.signUpMember(signUpRequest);

            // then
            assertThat(memberRepository.findByEmail("junho@test.com")).isNotEmpty();
        }

        @Test
        void 이미_가입된_이메일로_가입을_시도하면_예외() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);

            SignUpRequest request = new SignUpRequest(
                "주노",
                member.getEmail(),
                "123123"
            );

            // when & then
            assertThatThrownBy(() -> authService.signUpMember(request))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.ALREADY_EXIST_MEMBER.getMessage());
        }
    }

    @Nested
    class 사용자가_회원탈퇴를_수행한다 {

        @Test
        void 사용자가_회원탈퇴를_정상적으로_수행한다() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);

            // when
            memberService.deleteMember(member);

            // then
            assertThat(memberRepository.findById(member.getId())).isEmpty();
        }

        @Test
        void 진행중인_캠페인이_존재하면_예외() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);
            Campaign campaign = CampaignFixture.진행중인_보약_캠페인(member);
            campaignRepository.save(campaign);

            // when & then
            assertThatThrownBy(() -> memberService.deleteMember(member))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.MEMBER_INPROGRESS_CAMPAIGN_EXIST.getMessage());
        }

        @Test
        void 성공한_캠페인이_존재하면_정상수행() {
            // given
            Member member = MemberFixture.최준호();
            Campaign campaign = CampaignFixture.종료된_보약_캠페인(member, CampaignState.SUCCESS);
            Product product = ProductFixture.캠페인의_보약(campaign, member);
            Address address = AddressFixture.집주소(member);
            Order order = OrderFixture.주문하기(product, member, address);

            memberRepository.save(member);
            campaignRepository.save(campaign);
            productRepository.save(product);
            addressRepository.save(address);
            orderRepository.save(order);

            // when
            memberService.deleteMember(member);

            // then
            assertThat(memberRepository.findById(member.getId())).isEmpty();

            assertThat(campaignRepository.findById(campaign.getId())).isNotEmpty();
            assertThat(productRepository.findById(product.getId())).isNotEmpty();
            assertThat(addressRepository.findById(address.getId())).isNotEmpty();
            assertThat(orderRepository.findById(order.getId())).isNotEmpty();
        }

        @Test
        void 실패한_캠페인이_존재하면_정상수행() {
            // given
            Member member = MemberFixture.최준호();
            Campaign campaign = CampaignFixture.종료된_보약_캠페인(member, CampaignState.FAILED );
            Product product = ProductFixture.캠페인의_보약(campaign, member);
            Address address = AddressFixture.집주소(member);
            Order order = OrderFixture.주문하기(product, member, address);

            memberRepository.save(member);
            campaignRepository.save(campaign);
            productRepository.save(product);
            addressRepository.save(address);
            orderRepository.save(order);

            // when
            memberService.deleteMember(member);

            // then
            assertThat(memberRepository.findById(member.getId())).isEmpty();

            assertThat(campaignRepository.findById(campaign.getId())).isNotEmpty();
            assertThat(productRepository.findById(product.getId())).isNotEmpty();
            assertThat(addressRepository.findById(address.getId())).isNotEmpty();
            assertThat(orderRepository.findById(order.getId())).isNotEmpty();
        }
    }
}
