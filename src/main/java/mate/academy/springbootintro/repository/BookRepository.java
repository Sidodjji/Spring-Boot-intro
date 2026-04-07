package mate.academy.springbootintro.repository;

import java.util.List;
import mate.academy.springbootintro.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByCategoriesId(Long categoryId);
}
