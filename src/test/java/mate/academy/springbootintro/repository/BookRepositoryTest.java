package mate.academy.springbootintro.repository;

import mate.academy.springbootintro.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @Sql(scripts = {
            "classpath:database/books/add-book-to-books-table.sql",
            "classpath:database/categories/add-horror-category-to-categories-table.sql",
            "classpath:database/bookscategories/assign-categories-to-books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/bookscategories/delete-books-categories.sql",
            "classpath:database/books/delete-book.sql",
            "classpath:database/categories/delete-category.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("findAllByCategoriesId with wrong category id should return empty list")
    void findAllByCategoriesId_WithWrongCategoryId_ShouldReturnEmptyList() {
        List<Book> actual = bookRepository.findAllByCategoriesId(2L);

        assertTrue(actual.isEmpty());
    }
}