package mate.academy.bookstore.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.ISBN;

public record CreateBookRequestDto(
        @NotNull String title,
        @NotNull String author,
        @NotNull @ISBN String isbn,
        @NotNull @Min(0) BigDecimal price,
        String description,
        String coverImage
) {
}
