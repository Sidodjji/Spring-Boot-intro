package mate.academy.springbootintro.dto.cartitem;

import lombok.Data;
import mate.academy.springbootintro.model.Book;

@Data
public class CartItemDto {
    private Long id;
    private Book book;
    private int quantity;
}
