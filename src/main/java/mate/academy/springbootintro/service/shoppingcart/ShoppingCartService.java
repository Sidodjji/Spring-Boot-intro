package mate.academy.springbootintro.service.shoppingcart;

import mate.academy.springbootintro.dto.cartitem.CartItemDto;
import mate.academy.springbootintro.dto.cartitem.CreateCartItemRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {

    Page<CartItemDto> findAll(Pageable pageable);

    CartItemDto saveCartItem(CreateCartItemRequestDto requestDto);
}
