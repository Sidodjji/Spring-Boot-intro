package mate.academy.springbootintro.service.cartitem;

import mate.academy.springbootintro.dto.cartitem.CartItemDto;
import mate.academy.springbootintro.dto.cartitem.UpdateCartItemRequestDto;

public interface CartItemService {
    CartItemDto update(Long id, UpdateCartItemRequestDto requestDto);

    void deleteById(Long id);
}
