package cholog.wiseshop.api.address.controller;

import cholog.wiseshop.api.address.dto.request.CreateAddressRequest;
import cholog.wiseshop.api.address.dto.response.AddressResponse;
import cholog.wiseshop.api.address.service.AddressService;
import cholog.wiseshop.common.auth.Auth;
import cholog.wiseshop.db.member.Member;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/address")
@RestController
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<Long> createAddress(
        @Auth Member member,
        @RequestBody CreateAddressRequest request
    ) {
        Long response = addressService.createAddress(member, request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> readMemberAddresses(
        @Auth Member member
    ) {
        return ResponseEntity.ok().body(addressService.getMemberAddresses(member));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateAddressDefault(
        @Auth Member member,
        Long id
    ) {
        addressService.updateAddress(member, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@Auth Member member, @PathVariable Long id) {
        addressService.deleteAddress(member, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
