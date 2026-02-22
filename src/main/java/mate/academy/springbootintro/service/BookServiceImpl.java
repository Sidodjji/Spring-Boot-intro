package mate.academy.springbootintro.service;

import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.repository.BookRepository;

import java.util.List;

public class BookServiceImpl implements BookService{
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
