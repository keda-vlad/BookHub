package mate.academy.bookstore.dto.book;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class BookDto {
    private final Long id;
    private final String title;
    private final String author;
    private final String isbn;
    private final BigDecimal price;
    private final String description;
    private final String coverImage;
}
