package cholog.wiseshop.db.campaign;

import cholog.wiseshop.api.campaign.domain.CampaignModel;
import cholog.wiseshop.api.campaign.domain.CampaignRepository;
import cholog.wiseshop.api.member.domain.MemberModel;
import cholog.wiseshop.api.product.domain.ProductModel;
import cholog.wiseshop.db.member.Member;
import cholog.wiseshop.db.product.JdbcProductRepository;
import cholog.wiseshop.db.product.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CampaignRepositoryImpl implements CampaignRepository {

    private final JdbcCampaignRepository jdbcCampaignRepository;
    private final JdbcProductRepository jdbcProductRepository;

    public CampaignRepositoryImpl(JdbcCampaignRepository jdbcCampaignRepository, JdbcProductRepository jdbcProductRepository) {
        this.jdbcCampaignRepository = jdbcCampaignRepository;
        this.jdbcProductRepository = jdbcProductRepository;
    }

    @Override
    public CampaignModel findById(Long id) {
        Campaign campaign = jdbcCampaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 캠페인 입니다."));
        Member member = campaign.getMember();
        MemberModel memberModel = member.toModel();
        List<Product> products = jdbcProductRepository.findProductsByCampaignId(id);
        List<ProductModel> productModels = products.stream().map(it -> it.toModel(memberModel)).toList();

        return new CampaignModel(
                campaign.getId(),
                (long) campaign.getGoalQuantity(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                campaign.getState(),
                memberModel,
                productModels
        );
    }

    @Override
    public List<CampaignModel> findAll() {
        List<Campaign> campaigns = jdbcCampaignRepository.findAll();
        List<CampaignModel> result = new ArrayList<>();
        for (Campaign campaign : campaigns) {
            MemberModel memberModel = campaign.getMember().toModel();
            List<Product> products = jdbcProductRepository.findProductsByCampaignId(campaign.getId());
            List<ProductModel> productModels = products.stream().map(it -> it.toModel(memberModel)).toList();
            result.add(
                    new CampaignModel(
                            campaign.getId(),
                            (long) campaign.getGoalQuantity(),
                            campaign.getStartDate(),
                            campaign.getEndDate(),
                            campaign.getState(),
                            memberModel,
                            productModels
                    )
            );
        }
        return result;
    }
}
