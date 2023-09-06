package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.BookDto;
import mate.academy.bookstore.dto.BookSearchParametersDto;
import mate.academy.bookstore.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    List<BookDto> findAll();

    BookDto getById(Long id);

    void deleteById(Long id);

    void update(Long id, CreateBookRequestDto requestDto);

    List<BookDto> search(BookSearchParametersDto bookSearchParametersDto);
}
