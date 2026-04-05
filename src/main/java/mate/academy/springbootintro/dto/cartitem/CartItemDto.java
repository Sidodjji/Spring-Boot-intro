package mate.academy.springbootintro.dto.cartitem;

import lombok.Data;
import mate.academy.springbootintro.dto.book.BookDtoWithoutCategoryIds;

@Data
public class CartItemDto {
    private BookDtoWithoutCategoryIds book;
    private int quantity;
}
