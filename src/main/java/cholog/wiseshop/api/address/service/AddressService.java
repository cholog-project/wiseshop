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
        boolean existsDefaultAddress = addressRepository.findAllByMemberId(member.getId())
            .stream()
            .anyMatch(Address::isDefault);

        Address address = Address.from(member, request);
        address.setDefault(!existsDefaultAddress); // 첫 번째 주소만 기본 주소로 설정

        return addressRepository.save(address).getId();
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getMemberAddresses(Member member) {
        List<Address> addresses = addressRepository.findAllByMemberId(member.getId());
        return addresses.stream().map(it -> new AddressResponse(
            it.getId(),
            it.getPostalCode(),
            it.getRoadAddress(),
            it.getDetailAddress(),
            it.isDefault(),
            member.getId()
        )).toList();
    }

    public void updateAddress(Member member, Long addressId) {
        Address targetAddress = addressRepository.findById(addressId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ADDRESS_NOT_FOUND));
        if (!targetAddress.isOwner(member)) {
            throw new WiseShopException(WiseShopErrorCode.NOT_OWNER);
        }
        Address currentDefaultAddress = addressRepository.findAllByMemberId(member.getId())
            .stream()
            .filter(Address::isDefault)
            .findFirst()
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ADDRESS_NOT_FOUND));
        currentDefaultAddress.setDefault(false);
        addressRepository.save(currentDefaultAddress);
        targetAddress.setDefault(true);
        addressRepository.save(targetAddress);
    }

    public void deleteAddress(Member member, Long addressId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ADDRESS_NOT_FOUND));
        if (!address.isOwner(member)) {
            throw new WiseShopException(WiseShopErrorCode.NOT_OWNER);
        }
        if (address.isDefault()) {
            throw new WiseShopException(WiseShopErrorCode.DEFAULT_ADDRESS_NOT_DELETE);
        }
        addressRepository.deleteById(addressId);
    }
}
