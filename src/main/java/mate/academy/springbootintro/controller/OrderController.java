package mate.academy.springbootintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.order.CreateOrderRequestDto;
import mate.academy.springbootintro.dto.order.OrderDto;
import mate.academy.springbootintro.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.springbootintro.dto.orderitem.OrderItemDto;
import mate.academy.springbootintro.service.order.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders management", description = "Endpoints for managing orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Place an order")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public OrderDto createOrder(@RequestBody @Valid CreateOrderRequestDto requestDto) {
        return orderService.save(requestDto);
    }

    @Operation(summary = "Retrieve user's order history")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    private Page<OrderDto> getAll(Pageable pageable) {
        return orderService.findAllOrders(pageable);
    }

    @Operation(summary = "Update order status")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}")
    public OrderDto updateStatus(@RequestBody @Valid UpdateOrderStatusRequestDto requestDto,
                                 @PathVariable Long orderId) {
        return orderService.update(orderId, requestDto);
    }

    @Operation(summary = "Retrieve all order items for a specific order")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/items")
    public Page<OrderItemDto> getAllOrderItemInOrder(Pageable pageable,
                                                     @PathVariable Long id) {
        return orderService.findAllOrderItems(id, pageable);
    }

    @Operation(summary = "Retrieve a specific order item within an order")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{orderItemId}")
    public OrderItemDto getSpecificOrderItem(@PathVariable Long orderId,
                                             @PathVariable Long orderItemId) {
        return orderService.findSpecificOrderItem(orderId, orderItemId);
    }
}
