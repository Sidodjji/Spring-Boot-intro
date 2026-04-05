package mate.academy.springbootintro.service.shoppingcart;

import mate.academy.springbootintro.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.springbootintro.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootintro.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootintro.model.ShoppingCart;
import mate.academy.springbootintro.model.User;

public interface ShoppingCartService {
    ShoppingCartDto findAll();

    ShoppingCartDto saveCartItem(CreateCartItemRequestDto requestDto);

    ShoppingCartDto update(Long id, UpdateCartItemRequestDto requestDto);

    void deleteById(Long id);

    ShoppingCart createNewCart(User user);
}
