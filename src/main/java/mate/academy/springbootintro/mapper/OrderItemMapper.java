package mate.academy.springbootintro.mapper;

import mate.academy.springbootintro.config.MapperConfig;
import mate.academy.springbootintro.dto.orderitem.OrderItemDto;
import mate.academy.springbootintro.model.CartItem;
import mate.academy.springbootintro.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", source = "cartItem.book")
    @Mapping(target = "price", source = "cartItem.book.price")
    @Mapping(target = "quantity", source = "cartItem.quantity")
    @Mapping(target = "order", ignore = true)
    OrderItem toModel(CartItem cartItem);

    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);
}
