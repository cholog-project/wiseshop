package cholog.wiseshop.db.product;

import cholog.wiseshop.db.campaign.Campaign;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.stock JOIN FETCH p.campaign WHERE p.campaign.id = :campaignId")
    List<Product> findProductsByCampaignId(@Param("campaignId") Long campaignId);

    List<Product> findAllByCampaign(Campaign campaign);

    List<Product> findByOwnerId(Long memberId);
}
