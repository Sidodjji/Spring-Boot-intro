package mate.academy.springbootintro;

import java.math.BigDecimal;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootIntroApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootIntroApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book book = new Book();
                book.setAuthor("Taras");
                book.setPrice(BigDecimal.TEN);
                book.setTitle("BookOfBooks");
                book.setDescription("This is book");
                book.setIsbn("is");

                bookService.save(book);

                System.out.println(bookService.findAll());
            }
        };
    }

}
