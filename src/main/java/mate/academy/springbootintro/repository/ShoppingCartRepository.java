package mate.academy.springbootintro.repository;

import mate.academy.springbootintro.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    ShoppingCart findByUserId(Long id);
}
