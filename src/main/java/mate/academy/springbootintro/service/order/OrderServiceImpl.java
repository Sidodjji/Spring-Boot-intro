package mate.academy.springbootintro.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.order.CreateOrderRequestDto;
import mate.academy.springbootintro.dto.order.OrderDto;
import mate.academy.springbootintro.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.springbootintro.dto.orderitem.OrderItemDto;
import mate.academy.springbootintro.exeption.EmptyShoppingCartException;
import mate.academy.springbootintro.exeption.EntityNotFoundException;
import mate.academy.springbootintro.mapper.OrderItemMapper;
import mate.academy.springbootintro.mapper.OrderMapper;
import mate.academy.springbootintro.model.Order;
import mate.academy.springbootintro.model.OrderItem;
import mate.academy.springbootintro.model.ShoppingCart;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.repository.OrderItemRepository;
import mate.academy.springbootintro.repository.OrderRepository;
import mate.academy.springbootintro.repository.ShoppingCartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;

    private final OrderItemMapper orderItemMapper;

    private final OrderMapper orderMapper;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderDto save(CreateOrderRequestDto requestDto) {
        User user = getAuthenticatedUser();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart"));
        validateShoppingCart(shoppingCart);
        return orderMapper.toDto(orderRepository
                .save(createNewOrder(user, shoppingCart, requestDto)));
    }

    @Override
    public Page<OrderDto> findAllOrders(Pageable pageable) {
        User user = getAuthenticatedUser();
        return orderRepository.findAllByUserId(user.getId(), pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public Page<OrderItemDto> findAllOrderItems(Long orderId, Pageable pageable) {
        User user = getAuthenticatedUser();
        return orderItemRepository.findAllByOrderIdAndOrderUserId(orderId, user.getId(), pageable)
                .map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto findSpecificOrderItem(Long orderId, Long orderItemId) {
        User user = getAuthenticatedUser();
        return orderItemMapper.toDto(
                orderItemRepository.findByIdAndOrderIdAndOrderUserId(
                        orderItemId,
                        orderId,
                        user.getId()
                ));
    }

    @Override
    public OrderDto update(Long orderId, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId));
        order.setStatus(requestDto.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new RuntimeException("Can't find authenticated user");
    }

    private BigDecimal calculateTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> orderItem.getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order createNewOrder(
            User user,
            ShoppingCart shoppingCart,
            CreateOrderRequestDto requestDto
    ) {
        Order order = new Order();
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(orderItemMapper::toModel)
                .peek(orderItem -> orderItem.setOrder(order))
                .collect(Collectors.toSet());
        order.setOrderItemsSet(orderItems);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setTotal(calculateTotal(orderItems));
        return order;
    }

    private void validateShoppingCart(ShoppingCart shoppingCart) {
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new EmptyShoppingCartException("Can't create order from empty shopping cart");
        }
    }
}
