package mate.academy.springbootintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.springbootintro.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootintro.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootintro.service.shoppingcart.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Add cart item to shopping cart")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto addCartItemToShoppingCart(
            @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        return shoppingCartService.saveCartItem(requestDto);
    }

    @Operation(summary = "Get shopping cart")
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartService.findAll();
    }

    @Operation(summary = "Update books quantity in shopping cart")
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto updateCartItemQuantity(@PathVariable @Valid Long id,
            @RequestBody @Valid UpdateCartItemRequestDto requestDto) {
        return shoppingCartService.update(id, requestDto);
    }

    @Operation(summary = "Delete cart item by id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(@PathVariable Long id) {
        shoppingCartService.deleteById(id);
    }
}
