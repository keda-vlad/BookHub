package mate.academy.bookstore;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.CreateBookRequestDto;
import mate.academy.bookstore.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@SpringBootApplication
public class BookstoreApplication {
    private final BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            CreateBookRequestDto book = new CreateBookRequestDto();
            book.setTitle("The Lord of the Rings: The Return of the King");
            book.setAuthor("J. R. R. Tolkien");
            book.setIsbn("9780007136575");
            book.setPrice(BigDecimal.valueOf(199.99));
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
