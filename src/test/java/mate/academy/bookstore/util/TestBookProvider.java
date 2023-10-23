package mate.academy.bookstore.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;

public class TestBookProvider {
    public static List<Book> threeBooksFromDb() {
        return List.of(
                new Book()
                        .setId(1L)
                        .setTitle("The Hobbit")
                        .setAuthor("J.R.R. Tolkien")
                        .setIsbn("9780395647394")
                        .setPrice(BigDecimal.valueOf(20.22))
                        .setCategories(Set.of(new Category().setId(1L))),
                new Book()
                        .setId(2L)
                        .setTitle("Republic")
                        .setAuthor("Plato")
                        .setIsbn("9788830104716")
                        .setPrice(BigDecimal.valueOf(21.99))
                        .setCategories(Set.of(new Category().setId(2L))),
                new Book()
                        .setId(3L)
                        .setTitle("1984")
                        .setAuthor("George Orwell")
                        .setIsbn("9780007123810")
                        .setPrice(BigDecimal.valueOf(19.55))
                        .setCategories(Set.of(new Category().setId(3L)))
        );
    }

    public static CreateBookRequestDto updateRequestDto() {
        return new CreateBookRequestDto(
                "newTitle",
                "newAuthor",
                "9780007171996",
                BigDecimal.valueOf(22.22),
                null, null, Set.of()
        );
    }

    public static BookDto updateResponseDto(CreateBookRequestDto requestDto) {
        Book updatedBook = new Book()
                .setId(7L)
                .setTitle(requestDto.title())
                .setAuthor(requestDto.author())
                .setIsbn(requestDto.isbn())
                .setPrice(requestDto.price());
        return toBookDto(updatedBook);
    }

    public static Book validBook() {
        return new Book()
                .setTitle("valid_test_title")
                .setAuthor("valid_test_author")
                .setIsbn("9788845292613")
                .setPrice(BigDecimal.valueOf(20.20))
                .setDescription("test_description")
                .setCoverImage("test_cover_image")
                .setCategories(Set.of(new Category().setId(1L)));
    }

    public static CreateBookRequestDto toCreateBookRequestDto(Book book) {
        Set<Long> categoriesId
                = book.getCategories().stream().map(Category::getId).collect(Collectors.toSet());
        return new CreateBookRequestDto(
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPrice(),
                book.getDescription(),
                book.getCoverImage(),
                categoriesId
        );
    }

    public static BookDto toBookDto(Book book) {
        Set<Long> categoriesId
                = book.getCategories().stream().map(Category::getId).collect(Collectors.toSet());
        return new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategories(categoriesId);
    }

    public static BookSearchParametersDto toAuthorSearch(Book book) {
        return new BookSearchParametersDto(
                new String[]{},
                new String[]{book.getAuthor()},
                new String[]{}
        );
    }

    public static BookDtoWithoutCategoryIds toBookDtoWithoutCategoryIds(Book book) {
        return new BookDtoWithoutCategoryIds(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPrice(),
                book.getDescription(),
                book.getCoverImage()
        );
    }
}
