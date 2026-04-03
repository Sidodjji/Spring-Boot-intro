package mate.academy.springbootintro.service.book;

import java.util.List;
import mate.academy.springbootintro.dto.book.BookDto;
import mate.academy.springbootintro.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbootintro.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    Page<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id);
}
