package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    List<BookDto> findAll(Pageable pageable);

    BookDto getById(Long id);

    void deleteById(Long id);

    void update(Long id, CreateBookRequestDto requestDto);

    List<BookDto> search(BookSearchParametersDto bookSearchParametersDto, Pageable pageable);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id, Pageable pageable);
}
