package cholog.wiseshop.domain;

import static org.assertj.core.api.Assertions.assertThat;

import cholog.wiseshop.api.address.dto.CreateAddressRequest;
import cholog.wiseshop.api.address.service.AddressService;
import cholog.wiseshop.common.BaseTest;
import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.address.AddressRepository;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.fixture.AddressFixture;
import cholog.wiseshop.fixture.MemberFixture;
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

    }
}
