package mate.academy.springbootintro.service.book;

import mate.academy.springbootintro.dto.book.BookDto;
import mate.academy.springbootintro.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbootintro.dto.book.CreateBookRequestDto;
import mate.academy.springbootintro.exeption.EntityNotFoundException;
import mate.academy.springbootintro.mapper.BookMapper;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.model.Category;
import mate.academy.springbootintro.repository.BookRepository;
import mate.academy.springbootintro.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("findById with valid id should return BookDto")
    void findById_WithValidBookId_ShouldReturnBookDto() {
        Long bookId = 1L;

        Book book = new Book().setId(bookId);
        BookDto bookDto = new BookDto().setId(book.getId());

        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book))
                .thenReturn(bookDto);

        BookDto actual = bookService.findById(bookId);

        assertEquals(bookDto, actual);

        Mockito.verify(bookRepository).findById(bookId);
        Mockito.verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("findById with invalid id should throw EntityNotFoundException")
    void findById_WithInvalidBookId_ShouldThrowException() {
        Long bookId = 3L;

        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findById(bookId)
        );

        assertTrue(exception.getMessage().contains("3"));

        Mockito.verify(bookRepository).findById(bookId);
        Mockito.verifyNoInteractions(bookMapper);
    }

    @Test
    @DisplayName("findAll with empty books should return empty page")
    void findAll_WithEmptyBooks_ShouldReturnEmptyPage() {
        Pageable pageable = Pageable.unpaged();

        Mockito.when(bookRepository.findAll(pageable))
                .thenReturn(Page.empty());

        Page<BookDto> actual = bookService.findAll(pageable);

        assertTrue(actual.isEmpty());

        Mockito.verify(bookRepository).findAll(pageable);
        Mockito.verifyNoInteractions(bookMapper);
    }

    @Test
    @DisplayName("findAll with multiple books should return page with BookDto")
    void findAll_WithManyBooks_ShouldReturnPageWithBookDto() {
        Pageable pageable = PageRequest.of(0, 2);

        Book fistBook = new Book();
        Book secondBook = new Book();

        Page<Book> page = new PageImpl<>(List.of(fistBook, secondBook), pageable, 2);

        BookDto firstDto = new BookDto();
        BookDto secondDto = new BookDto();

        Mockito.when(bookRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(bookMapper.toDto(fistBook)).thenReturn(firstDto);
        Mockito.when(bookMapper.toDto(secondBook)).thenReturn(secondDto);

        Page<BookDto> actual = bookService.findAll(pageable);

        assertEquals(2, actual.getContent().size());

        Mockito.verify(bookRepository).findAll(pageable);
        Mockito.verify(bookMapper, Mockito.times(2)).toDto(Mockito.any());
    }

    @Test
    @DisplayName("save with valid request should return BookDto")
    void save_WithValidRequest_ShouldReturnBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setCategoryIds(Set.of(1L, 2L));

        Book book = new Book();
        Category firstCategory = new Category().setId(1L);
        Category secondCategory = new Category().setId(2L);

        Book savedBook = new Book().setId(10L);
        BookDto expectedDto = new BookDto().setId(10L);

        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(List.of(firstCategory, secondCategory));
        Mockito.when(bookRepository.save(book)).thenReturn(savedBook);
        Mockito.when(bookMapper.toDto(savedBook)).thenReturn(expectedDto);

        BookDto result = bookService.save(requestDto);

        Assertions.assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(2, book.getCategories().size());

        Mockito.verify(bookRepository).save(book);
        Mockito.verify(categoryRepository).findAllById(requestDto.getCategoryIds());
        Mockito.verify(bookMapper).toModel(requestDto);
        Mockito.verify(bookMapper).toDto(savedBook);
    }

    @Test
    @DisplayName("update with valid request should return updated BookDto")
    void update_WithValidRequest_ShouldReturnBookDto() {
        Long requestId = 1L;

        Book book = new Book()
                .setId(requestId)
                .setTitle("Book A");

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Book B");

        Book updatedBook = new Book()
                .setId(requestId)
                .setTitle("Book B");

        BookDto bookDto = new BookDto()
                .setId(updatedBook.getId())
                .setTitle(updatedBook.getTitle());

        Mockito.when(bookRepository.findById(requestId))
                .thenReturn(Optional.of(book));

        Mockito.doAnswer(invocation -> {
            Book target = invocation.getArgument(1);
            target.setTitle(requestDto.getTitle());
            return null;
        }).when(bookMapper).updateBookFromDto(requestDto, book);

        Mockito.when(bookRepository.save(book)).thenReturn(updatedBook);
        Mockito.when(bookMapper.toDto(updatedBook)).thenReturn(bookDto);

        BookDto actual = bookService.update(requestId, requestDto);

        assertEquals(bookDto, actual);

        Mockito.verify(bookRepository).findById(requestId);
        Mockito.verify(bookRepository).save(book);
        Mockito.verify(bookMapper).toDto(updatedBook);
    }

    @Test
    @DisplayName("update with non existing book should throw EntityNotFoundException")
    void update_WithNonExistingBook_ShouldThrowException() {
        Long requestId = 1L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Book B");

        Mockito.when(bookRepository.findById(requestId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(requestId, requestDto)
        );

        assertTrue(exception.getMessage().contains("1"));

        Mockito.verify(bookRepository).findById(requestId);
    }

    @Test
    @DisplayName("findAllByCategoryId with books should return list of BookDtoWithoutCategoryIds")
    void findAllByCategoryId_WithBooks_ShouldReturnListOfBookDtoWithoutCategoryIds() {
        Long requestId = 1L;

        Book firstBook = new Book();
        Book secondBook = new Book();
        List<Book> bookList = List.of(firstBook, secondBook);

        BookDtoWithoutCategoryIds firstDto = new BookDtoWithoutCategoryIds();
        BookDtoWithoutCategoryIds secondDto = new BookDtoWithoutCategoryIds();
        List<BookDtoWithoutCategoryIds> dtoList = List.of(firstDto, secondDto);

        Mockito.when(bookRepository.findAllByCategoriesId(requestId)).thenReturn(bookList);
        Mockito.when(bookMapper.toDtoWithoutCategories(bookList)).thenReturn(dtoList);

        List<BookDtoWithoutCategoryIds> result =
                bookService.findAllByCategoryId(requestId);

        assertEquals(dtoList, result);

        Mockito.verify(bookRepository).findAllByCategoriesId(requestId);
        Mockito.verify(bookMapper).toDtoWithoutCategories(bookList);
    }

    @Test
    @DisplayName("findAllByCategoryId with no books should return empty list")
    void findAllByCategoryId_WithNoBooks_ShouldReturnEmptyList() {
        Long requestId = 1L;

        Mockito.when(bookRepository.findAllByCategoriesId(requestId))
                .thenReturn(List.of());
        Mockito.when(bookMapper.toDtoWithoutCategories(List.of()))
                .thenReturn(List.of());

        List<BookDtoWithoutCategoryIds> result =
                bookService.findAllByCategoryId(requestId);

        assertTrue(result.isEmpty());

        Mockito.verify(bookRepository).findAllByCategoriesId(requestId);
        Mockito.verify(bookMapper).toDtoWithoutCategories(List.of());
    }
}