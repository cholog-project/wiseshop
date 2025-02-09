package cholog.wiseshop.api.address.service;

import cholog.wiseshop.api.address.dto.CreateAddressRequest;
import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.address.AddressRepository;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;

    public AddressService(AddressRepository addressRepository, OrderRepository orderRepository) {
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
    }

    public Long createAddress(Member member, CreateAddressRequest request) {
        Address address = Address.from(member, request);
        return addressRepository.save(address).getId();
    }

    public void deleteAddress(Member member, Long addressId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ADDRESS_NOT_FOUND));
        List<Order> orders = orderRepository.findByAddressId(address.getId());
        /*
        * 아래의 배송 정보 삭제 정책 이상해요 주문한 상품이 배송 완료, 배송 중인지를 모르니
        * 그냥 주문 정보 있으면 삭제가 불가능하게 밖에 못했습니다.
         */
        if (orders.stream().anyMatch(it -> it.getAddress().equals(address))) {
            throw new WiseShopException(WiseShopErrorCode.ADDRESS_EXIST_INTO_ORDER);
        }
    }
}
