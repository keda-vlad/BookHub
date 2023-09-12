package mate.academy.bookstore.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.ISBN;

@Data
public class CreateBookRequestDto {
    private final @NotNull String title;
    private final @NotNull String author;
    private final @NotNull @ISBN String isbn;
    private final @NotNull @Min(0) BigDecimal price;
    private final String description;
    private final String coverImage;
}
