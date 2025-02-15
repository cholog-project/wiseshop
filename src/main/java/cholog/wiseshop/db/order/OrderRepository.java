package cholog.wiseshop.db.order;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMemberId(Long memberId);
    Optional<Order> findByIdAndMemberId(Long id, Long memberId);
}
