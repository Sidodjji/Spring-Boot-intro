package mate.academy.springbootintro.mapper;

import java.util.List;
import mate.academy.springbootintro.config.MapperConfig;
import mate.academy.springbootintro.dto.book.BookDto;
import mate.academy.springbootintro.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbootintro.dto.book.CreateBookRequestDto;
import mate.academy.springbootintro.model.Book;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(CreateBookRequestDto dto, @MappingTarget Book book);

    List<BookDtoWithoutCategoryIds> toDtoWithoutCategories(List<Book> book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        bookDto.setCategory(book.getCategories());
    }
}
