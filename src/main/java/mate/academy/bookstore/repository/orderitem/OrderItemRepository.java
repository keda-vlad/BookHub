package mate.academy.bookstore.repository.orderitem;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
              FROM OrderItem oi
              LEFT JOIN FETCH oi.book b
              LEFT JOIN FETCH b.categories c
              WHERE oi.order.user.id = :userId
              AND oi.order.id = :orderId
              """)
    List<OrderItem> findAllByUserIdAndOrderId(Long userId, Long orderId, Pageable pageable);

    @Query("""
              FROM OrderItem oi
              LEFT JOIN FETCH oi.book b
              LEFT JOIN FETCH b.categories c
              WHERE oi.order.user.id = :userId
              AND oi.order.id = :orderId
              AND oi.id = :itemId
              """)
    Optional<OrderItem> findByUserIdAndItemIdAndOrderId(Long userId, Long itemId, Long orderId);
}
