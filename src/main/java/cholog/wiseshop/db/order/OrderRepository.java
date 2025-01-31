package cholog.wiseshop.db.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMemberId(Long memberId);
}
