package mate.academy.springbootintro.repository;

import mate.academy.springbootintro.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findAllByOrderIdAndOrderUserId(Long orderId, Long userId, Pageable pageable);

    OrderItem findByIdAndOrderIdAndOrderUserId(Long orderItemId, Long orderId, Long userId);
}
