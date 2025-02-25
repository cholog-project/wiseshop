package cholog.wiseshop.api.order.service;

import cholog.wiseshop.api.order.dto.request.CreateOrderRequest;
import cholog.wiseshop.api.order.dto.response.MemberOrderResponse;
import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.address.AddressRepository;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.exception.WiseShopErrorCode;
import cholog.wiseshop.exception.WiseShopException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    public OrderService(
        OrderRepository orderRepository,
        ProductRepository productRepository,
        AddressRepository addressRepository
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
    }

    public Long createOrder(CreateOrderRequest request, Member member) {
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.PRODUCT_NOT_FOUND));
        Address address = addressRepository.findById(request.addressId())
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ORDER_NOT_FOUND));
        Campaign campaign = product.getCampaign();
        validateCampaignStateInProgress(campaign);
        Stock stock = product.getStock();
        validateQuantity(campaign, stock, request.orderQuantity());
        Member campaignOwner = campaign.getMember().orElse(Member.createEmpty());
        validateOrderOwner(campaignOwner, member);
        Order order = orderRepository.save(request.from(product, member, address));
        campaign.increaseSoldQuantity(request.orderQuantity());
        return order.getId();
    }

    @Transactional(readOnly = true)
    public MemberOrderResponse readOrder(Member member, Long id) {
        Order order = orderRepository.findByIdAndMemberId(id, member.getId())
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ORDER_NOT_FOUND));
        return new MemberOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<MemberOrderResponse> readMemberOrders(Member member) {
        return orderRepository.findAllByMemberId(member.getId()).stream()
            .map(MemberOrderResponse::new)
            .toList();
    }

    public void deleteOrder(Member member, Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new WiseShopException(WiseShopErrorCode.ORDER_NOT_FOUND));
        if (!order.isOwner(member)) {
            throw new WiseShopException(WiseShopErrorCode.NOT_OWNER);
        }
        Campaign campaign = order.getProduct().getCampaign();
        validateCampaignStateInProgress(campaign);
        campaign.cancelSoldQuantity(order.getCount());
        orderRepository.deleteById(id);
    }

    public void validateQuantity(Campaign campaign, Stock stock, int orderQuantity) {
        int remainQuantity = stock.getTotalQuantity() - campaign.getSoldQuantity();

        if (remainQuantity - orderQuantity < 0) {
            throw new WiseShopException(WiseShopErrorCode.ORDER_LIMIT_EXCEED, remainQuantity);
        }
    }

    public void validateCampaignStateInProgress(Campaign campaign) {
        if (!campaign.isInProgress()) {
            throw new WiseShopException(WiseShopErrorCode.CAMPAIGN_NOT_IN_PROGRESS);
        }
    }

    public void validateOrderOwner(Member campaignOwner, Member orderMember) {
        if (Objects.equals(campaignOwner.getId(), orderMember.getId())) {
            throw new WiseShopException(WiseShopErrorCode.ORDER_NOT_AVAILABLE);
        }
    }
}
