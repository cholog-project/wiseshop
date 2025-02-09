package cholog.wiseshop.api.address.service;

import cholog.wiseshop.api.address.dto.CreateAddressRequest;
import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.address.AddressRepository;
import cholog.wiseshop.db.member.Member;
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
}
