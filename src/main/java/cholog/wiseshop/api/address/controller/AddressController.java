package cholog.wiseshop.api.address.controller;

import cholog.wiseshop.api.address.dto.CreateAddressRequest;
import cholog.wiseshop.api.address.service.AddressService;
import cholog.wiseshop.common.auth.Auth;
import cholog.wiseshop.db.member.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/member/address")
    public ResponseEntity<Long> createAddress(
        @Auth Member member,
        @RequestBody CreateAddressRequest request
    ) {
        return ResponseEntity.ok().body(addressService.createAddress(member, request));
    }

    @DeleteMapping("/member/address/{id}")
    public ResponseEntity<Void> deleteAddress(@Auth Member member, @PathVariable Long id) {
        addressService.deleteAddress(member, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
