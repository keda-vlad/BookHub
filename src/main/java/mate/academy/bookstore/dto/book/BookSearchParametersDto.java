package mate.academy.bookstore.dto.book;

import lombok.Data;

@Data
public class BookSearchParametersDto {
    private final String[] titles;
    private final String[] authors;
    private final String[] prices;
}
