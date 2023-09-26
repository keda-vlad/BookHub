package mate.academy.bookstore.repository.order;

import java.util.List;
import mate.academy.bookstore.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
    FROM Order o
    LEFT JOIN FETCH o.user u
    LEFT JOIN FETCH o.orderItems oi
    LEFT JOIN FETCH oi.book
    WHERE u.id = :userId
                """)
    List<Order> findAllByUserId(Long userId, Pageable pageable);
}
