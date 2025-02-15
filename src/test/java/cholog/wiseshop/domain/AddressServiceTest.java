package cholog.wiseshop.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cholog.wiseshop.api.address.dto.request.CreateAddressRequest;
import cholog.wiseshop.api.address.dto.response.MemberAddressListResponse;
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
public class AddressServiceTest extends BaseTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AddressService addressService;

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
            MemberAddressListResponse response = addressService.readMemberAddresses(member);

            // then
            assertThat(response.responses()).hasSize(2);
        }
    }

    @Nested
    class 사용자가_배송지를_삭제한다 {

        @Test
        void 사용자가_배송지를_정상적으로_삭제한다() {
            // given
            Member member = MemberFixture.최준호();
            memberRepository.save(member);
            Address address = AddressFixture.집주소(member);
            addressRepository.save(address);

            // when
            addressService.deleteAddress(member, address.getId());

            // then
            assertThat(addressRepository.findById(address.getId())).isEmpty();
        }
    }

    @Test
    void 배송지_소유가_삭제를_요청한_사용자와_다르면_예외_발생() {
        // given
        Member junho = MemberFixture.최준호();
        Member junesoo = MemberFixture.김준수();
        memberRepository.save(junho);
        Address address = AddressFixture.집주소(junho);
        addressRepository.save(address);

        // when & then
        assertThatThrownBy(() -> addressService.deleteAddress(junesoo, address.getId()))
            .isInstanceOf(WiseShopException.class)
            .hasMessage(WiseShopErrorCode.ADDRESS_OWNER_MISMATCH.getMessage());
    }
}
