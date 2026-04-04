package mate.academy.springbootintro.service.shoppingcart;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.cartitem.CartItemDto;
import mate.academy.springbootintro.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.springbootintro.exeption.EntityNotFoundException;
import mate.academy.springbootintro.mapper.CartItemMapper;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.model.CartItem;
import mate.academy.springbootintro.model.ShoppingCart;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.repository.BookRepository;
import mate.academy.springbootintro.repository.CartItemRepository;
import mate.academy.springbootintro.repository.ShoppingCartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final CartItemRepository cartItemRepository;

    private final ShoppingCartRepository shoppingCartRepository;

    private final BookRepository bookRepository;

    private final CartItemMapper cartItemMapper;

    @Override
    public Page<CartItemDto> findAll(Pageable pageable) {
        User user = getAuthenticatedUser();
        return cartItemRepository.findByShoppingCartUserId(user.getId(), pageable)
                .map(cartItemMapper::toDto);
    }

    @Override
    public CartItemDto saveCartItem(CreateCartItemRequestDto requestDto) {
        User user = getAuthenticatedUser();

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> createNewCart(user));

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book by id: " + requestDto.getBookId()));

        CartItem cartItem = cartItemRepository
                .findByShoppingCartIdAndBookId(shoppingCart.getId(), book.getId())
                .orElseGet(() -> createNewCartItem(shoppingCart, book));

        cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    private ShoppingCart createNewCart(User user) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(user);
        return shoppingCartRepository.save(newCart);
    }

    private CartItem createNewCartItem(ShoppingCart cart, Book book) {
        CartItem newItem = new CartItem();
        newItem.setShoppingCart(cart);
        newItem.setBook(book);
        newItem.setQuantity(0);
        return newItem;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new RuntimeException("Can't find authenticated user");
    }
}
