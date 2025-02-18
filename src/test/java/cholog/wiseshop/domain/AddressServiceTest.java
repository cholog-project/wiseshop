package cholog.wiseshop.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.address.dto.request.CreateAddressRequest;
import cholog.wiseshop.api.address.dto.response.AddressResponse;
import cholog.wiseshop.api.address.service.AddressService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.address.AddressRepository;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import cholog.wiseshop.fixture.AddressFixture;
import cholog.wiseshop.fixture.MemberFixture;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class AddressServiceTest extends BaseTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AddressService addressService;

    @Test
    void 배송지_소유자가_삭제를_요청한_사용자와_다르면_예외_발생() {
        // given
        Member junho = MemberFixture.최준호();
        Member junesoo = MemberFixture.김준수();
        memberRepository.save(junho);
        Address address = AddressFixture.집주소(junho);
        addressRepository.save(address);

        // when & then
        assertThatThrownBy(() -> addressService.deleteAddress(junesoo, address.getId()))
            .isInstanceOf(WiseShopException.class)
            .hasMessage(WiseShopErrorCode.NOT_OWNER.getMessage());
    }

    @Nested
    class 사용자가_배송지를_등록한다 {

        @Test
        void 사용자가_배송지를_정상적으로_등록한다() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);
            CreateAddressRequest request = new CreateAddressRequest(
                06160,
                "서울특별시 강남구 삼성동 142-35",
                "13층",
                true
            );

            // when
            Long addressId = addressService.createAddress(member, request);

            // then
            assertThat(addressRepository.findById(addressId)).isNotEmpty();
        }

        @Test
        void 기본_배송지는_오직_1개여야_한다() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);

            CreateAddressRequest home = new CreateAddressRequest(
                06160,
                "서울특별시 강남구 삼성동 142-35",
                "13층",
                true
            );

            CreateAddressRequest company = new CreateAddressRequest(
                06160,
                "서울 송파구 올림픽로 35다길 42",
                "루터회관 14층",
                true
            );

            // when
            addressService.createAddress(member, home);
            addressService.createAddress(member, company);

            // then
            List<Address> addresses = addressRepository.findAllByMemberId(member.getId())
                .stream()
                .filter(Address::isDefault)
                .toList();

            assertThat(addresses.size()).isEqualTo(1);
        }
    }

    @Nested
    class 사용자가_배송지를_조회한다 {

        @Test
        void 사용자가_등록한_배송지들을_정상적으로_조회한다() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);
            Address home = AddressFixture.집주소(member);
            Address company = AddressFixture.집주소(member);
            addressRepository.saveAll(List.of(home, company));

            // when
            List<AddressResponse> response = addressService.getMemberAddresses(member);

            // then
            assertThat(response).hasSize(2);
        }
    }

    @Nested
    class 사용자가_배송지를_수정한다 {

        @Test
        void 사용자가_기본_배송지를_정상적으로_수정한다() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);
            Address home = AddressFixture.집주소(member);
            Address company = AddressFixture.회사주소(member);
            List<Address> addresses = addressRepository.saveAll(List.of(home, company));

            // when
            System.out.println(member.getName());
            System.out.println(addresses.getLast().getMember().get().getName());
            addressService.updateAddress(member, addresses.getLast().getId());
            Address address = addressRepository.findById(addresses.getLast().getId())
                .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ADDRESS_NOT_FOUND));

            // then
            assertThat(address.isDefault()).isTrue();
        }

        @Test
        void 자신의_배송지가_아닌_다른_배송지를_수정하면_예외() {
            // given
            Member junho = MemberFixture.최준호();
            Member june = MemberFixture.김준수();
            memberRepository.saveAll(List.of(june, junho));
            Address address = AddressFixture.집주소(junho);
            Address company = AddressFixture.회사주소(junho);
            addressRepository.saveAll(List.of(address, company));

            // when & then
            assertThatThrownBy(() -> addressService.updateAddress(june, address.getId()))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.NOT_OWNER.getMessage());
        }
    }

    @Nested
    class 사용자가_배송지를_삭제한다 {

        @Test
        void 사용자가_기본_배송지가_아닌_다른_배송지를_정상적으로_삭제한다() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);
            Address home = AddressFixture.집주소(member);
            Address company = AddressFixture.회사주소(member);
            addressRepository.saveAll(List.of(home, company));

            // when
            addressService.deleteAddress(member, company.getId());

            // then
            assertThat(addressRepository.findById(company.getId())).isEmpty();
        }

        @Test
        void 자신의_배송지가_아닌_다른_배송지를_삭제하면_예외() {
            // given
            Member junho = MemberFixture.최준호();
            Member june = MemberFixture.김준수();
            memberRepository.saveAll(List.of(june, junho));
            Address address = AddressFixture.집주소(junho);
            addressRepository.save(address);

            // when & then
            assertThatThrownBy(() -> addressService.deleteAddress(june, address.getId()))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.NOT_OWNER.getMessage());
        }

        @Test
        void 기본_배송지를_삭제하면_예외() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);
            Address address = AddressFixture.집주소(member);
            addressRepository.save(address);

            // when & then
            assertThatThrownBy(() -> addressService.deleteAddress(member, address.getId()))
                .isInstanceOf(WiseShopException.class)
                .hasMessage(WiseShopErrorCode.DEFAULT_ADDRESS_NOT_DELETE.getMessage());
        }
    }
}
