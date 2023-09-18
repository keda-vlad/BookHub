package mate.academy.bookstore.repository.cartitem;

import java.util.Optional;
import mate.academy.bookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("FROM CartItem ci WHERE ci.shoppingCart.id = :cartId AND ci.book.id = :bookId")
    Optional<CartItem> findByShoppingCartIdAndBookId(
            @Param("cartId") Long cartId, @Param("bookId") Long bookId
    );
}
