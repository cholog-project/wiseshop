
 package cholog.wiseshop.common;

import cholog.wiseshop.db.address.Address;
import cholog.wiseshop.db.address.AddressRepository;
import cholog.wiseshop.db.campaign.Campaign;
import cholog.wiseshop.db.campaign.CampaignRepository;
import cholog.wiseshop.db.campaign.CampaignState;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.member.MemberRepository;
import cholog.wiseshop.db.product.Product;
import cholog.wiseshop.db.product.ProductRepository;
import cholog.wiseshop.db.stock.Stock;
import cholog.wiseshop.db.stock.StockRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
public class TestDataLoader implements CommandLineRunner {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {
//        Member supplier = memberRepository.save(new Member(
//            "supplier@test.com",
//            "판매자",
//            passwordEncoder.encode("12341234")
//        ));
//        Member customer = memberRepository.save(new Member(
//            "customer@test.com",
//            "구매자",
//            passwordEncoder.encode("12341234")
//        ));
//        addressRepository.save(new Address(
//            06160,
//            "서울특별시 강남구 삼성동 142-35",
//            "13층",
//            true,
//            customer
//        ));
//        LocalDateTime now = LocalDateTime.now();
//        Campaign campaign = campaignRepository.save(Campaign.builder()
//            .startDate(now.plusDays(1))
//            .endDate(now.plusDays(2))
//            .goalQuantity(100)
//            .soldQuantity(0)
//            .state(CampaignState.IN_PROGRESS)
//            .member(supplier)
//            .now(now)
//            .build()
//        );
//        Stock stock = stockRepository.save(new Stock(500));
//        productRepository.save(Product.builder()
//            .name("에그타르트")
//            .description("에그타르트 맛있어요")
//            .price(5000)
//            .stock(stock)
//            .campaign(campaign)
//            .owner(supplier)
//            .build()
//        );
    }
}
