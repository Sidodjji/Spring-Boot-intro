package mate.academy.springbootintro.service;

import mate.academy.springbootintro.model.Book;

import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
