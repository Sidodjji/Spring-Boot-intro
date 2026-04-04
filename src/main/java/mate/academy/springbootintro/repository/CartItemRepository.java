package mate.academy.springbootintro.repository;

import java.util.Optional;
import mate.academy.springbootintro.model.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByShoppingCartIdAndBookId(Long shoppingCartId, Long bookId);

    Page<CartItem> findByShoppingCartUserId(Long id, Pageable pageable);
}
