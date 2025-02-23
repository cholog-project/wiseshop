package cholog.wiseshop.db.product;

import cholog.wiseshop.db.campaign.Campaign;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.stock JOIN FETCH p.campaign WHERE p.campaign.id = :campaignId")
    List<Product> findAllByCampaignId(@Param("campaignId") Long campaignId);

    List<Product> findAllByCampaign(Campaign campaign);

    List<Product> findAllByOwnerId(Long memberId);

    @Query(value = "SELECT p FROM Product p JOIN FETCH p.stock JOIN FETCH p.campaign WHERE p.name LIKE %:keyword%",
        countQuery = "SELECT COUNT(p) FROM Product p WHERE p.name LIKE %:keyword%")
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
}
