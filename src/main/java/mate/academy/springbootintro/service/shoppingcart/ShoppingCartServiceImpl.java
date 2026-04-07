package mate.academy.springbootintro.service.shoppingcart;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.springbootintro.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbootintro.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbootintro.exeption.EntityNotFoundException;
import mate.academy.springbootintro.mapper.CartItemMapper;
import mate.academy.springbootintro.mapper.ShoppingCartMapper;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.model.CartItem;
import mate.academy.springbootintro.model.ShoppingCart;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.repository.BookRepository;
import mate.academy.springbootintro.repository.CartItemRepository;
import mate.academy.springbootintro.repository.ShoppingCartRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartItemRepository cartItemRepository;

    private final ShoppingCartRepository shoppingCartRepository;

    private final BookRepository bookRepository;

    private final CartItemMapper cartItemMapper;

    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto findAll() {
        User user = getAuthenticatedUser();
        return shoppingCartMapper.toDto(shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart")));
    }

    @Override
    public ShoppingCartDto saveCartItem(CreateCartItemRequestDto requestDto) {
        User user = getAuthenticatedUser();

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart"));

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book by id: " + requestDto.getBookId()));

        CartItem cartItem = cartItemRepository
                .findByShoppingCartIdAndBookId(shoppingCart.getId(), book.getId())
                .map(item -> {
                    item.setQuantity(item.getQuantity() + requestDto.getQuantity());
                    return item;
                })
                .orElseGet(() -> {
                    CartItem newCartItem = cartItemMapper.toModel(requestDto);
                    newCartItem.setShoppingCart(shoppingCart);
                    newCartItem.setBook(book);
                    return newCartItem;
                });
        cartItemRepository.save(cartItem);

        return shoppingCartMapper.toDto(shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart")));
    }

    @Override
    public ShoppingCartDto update(Long id, UpdateCartItemRequestDto requestDto) {
        User user = getAuthenticatedUser();
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart item"));

        cartItemMapper.updateCartItemFromDto(requestDto, cartItem);
        cartItemRepository.save(cartItem);

        return shoppingCartMapper.toDto(shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart")));
    }

    @Override
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }

    @Override
    public ShoppingCart createNewCart(User user) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(user);
        return shoppingCartRepository.save(newCart);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new RuntimeException("Can't find authenticated user");
    }
}
