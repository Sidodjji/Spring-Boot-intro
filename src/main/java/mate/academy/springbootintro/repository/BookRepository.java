package mate.academy.springbootintro.repository;

import java.util.List;
import mate.academy.springbootintro.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
