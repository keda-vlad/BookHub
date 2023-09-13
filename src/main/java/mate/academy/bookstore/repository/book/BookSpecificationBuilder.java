package mate.academy.bookstore.repository.book;

import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.SpecificationBuilder;

public interface BookSpecificationBuilder
        extends SpecificationBuilder<Book, BookSearchParametersDto> {
}
