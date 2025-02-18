package cholog.wiseshop.db.campaign;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findAllByMemberId(Long memberId);

    @Query("SELECT c FROM Campaign c WHERE c.state IN :states")
    List<Campaign> findAllByStates(List<CampaignState> states);
}
