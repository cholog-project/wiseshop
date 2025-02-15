package cholog.wiseshop.api.address.service;

import cholog.wiseshop.api.address.dto.request.CreateAddressRequest;
import cholog.wiseshop.api.address.dto.response.AddressResponse;
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

    public void deleteAddress(Member member, Long addressId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ADDRESS_NOT_FOUND));
        address.validatesOwner(member);
        addressRepository.deleteById(addressId);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAll(Member member) {
        List<Address> addresses = addressRepository.findAllByMemberId(member.getId());
        return addresses.stream().map(it -> new AddressResponse(
            it.getId(),
            it.getPostalCode(),
            it.getRoadAddress(),
            it.getDetailAddress(),
            it.isDefault()
        )).toList();
    }
}
