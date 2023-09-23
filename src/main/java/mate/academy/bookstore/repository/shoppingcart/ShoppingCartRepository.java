package mate.academy.bookstore.repository.shoppingcart;

import java.util.Optional;
import mate.academy.bookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("""
    FROM ShoppingCart sc
    LEFT JOIN FETCH sc.user u
    LEFT JOIN FETCH sc.cartItems ci
    LEFT JOIN FETCH u.roles r
    LEFT JOIN FETCH ci.book b
    WHERE sc.id = :cartId
                """)
    Optional<ShoppingCart> findByCartId(Long cartId);

    @Query("""
    FROM ShoppingCart sc
    LEFT JOIN FETCH sc.user u
    LEFT JOIN FETCH sc.cartItems ci
    LEFT JOIN FETCH u.roles r
    LEFT JOIN FETCH ci.book b
    WHERE u.id = :userId
                """)
    Optional<ShoppingCart> findByUserId(Long userId);
}
