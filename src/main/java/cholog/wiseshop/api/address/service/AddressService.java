package cholog.wiseshop.api.address.service;

import cholog.wiseshop.api.address.dto.request.CreateAddressRequest;
import cholog.wiseshop.api.address.dto.response.MemberAddressListResponse;
import cholog.wiseshop.api.address.dto.response.MemberAddressResponse;
import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.address.AddressRepository;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Long createAddress(Member member, CreateAddressRequest request) {
        Address address = Address.from(member, request);
        return addressRepository.save(address).getId();
    }

    public MemberAddressListResponse readMemberAddresses(Member member) {
        List<MemberAddressResponse> memberAddresses = addressRepository.findAllByMemberId(member.getId())
            .stream().map(MemberAddressResponse::new).toList();
        return new MemberAddressListResponse(memberAddresses);
    }

    public void deleteAddress(Member member, Long addressId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ADDRESS_NOT_FOUND));
        address.validatesOwner(member);
        addressRepository.deleteById(addressId);
    }
}
