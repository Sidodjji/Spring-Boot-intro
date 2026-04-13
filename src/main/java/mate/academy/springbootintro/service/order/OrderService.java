package mate.academy.springbootintro.service.order;

import mate.academy.springbootintro.dto.order.CreateOrderRequestDto;
import mate.academy.springbootintro.dto.order.OrderDto;
import mate.academy.springbootintro.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.springbootintro.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderDto> findAllOrders(Pageable pageable);

    Page<OrderItemDto> findAllOrderItems(Long orderId, Pageable pageable);

    OrderItemDto findSpecificOrderItem(Long orderId, Long itemId);

    OrderDto update(Long orderId, UpdateOrderStatusRequestDto requestDto);

    OrderDto save(CreateOrderRequestDto requestDto);
}
