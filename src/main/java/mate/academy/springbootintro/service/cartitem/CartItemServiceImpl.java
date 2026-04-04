package mate.academy.springbootintro.service.cartitem;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.cartitem.CartItemDto;
import mate.academy.springbootintro.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootintro.exeption.EntityNotFoundException;
import mate.academy.springbootintro.mapper.CartItemMapper;
import mate.academy.springbootintro.model.CartItem;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.repository.CartItemRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemMapper cartItemMapper;

    private final CartItemRepository cartItemRepository;

    @Override
    public CartItemDto update(Long id, UpdateCartItemRequestDto requestDto) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart item"));

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!cartItem.getShoppingCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        cartItemMapper.updateCartItemFromDto(requestDto, cartItem);

        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }
}
