package mate.academy.bookstore.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.book.BookSpecificationBuilder;
import mate.academy.bookstore.repository.book.spec.AuthorSpecificationProvider;
import mate.academy.bookstore.repository.category.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Verify save() method works for BookServiceImpl")
    public void saveBook_ValidRequestDto_ReturnBookDto() {
        //Given
        Book book = validBook();
        CreateBookRequestDto requestDto = validRequestDto(book);
        BookDto bookDto = validBookDto(book).setCategories(requestDto.categories());

        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(categoryRepository.findAllByIdIn(Mockito.anySet()))
                .thenReturn(book.getCategories());
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        BookDto savedBookDto = bookService.save(requestDto);

        //Then
        Assertions.assertEquals(bookDto, savedBookDto);
        Mockito.verify(bookMapper, Mockito.times(1)).toModel(Mockito.any());
        Mockito.verify(bookMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(categoryRepository, Mockito.times(1)).findAllByIdIn(Mockito.any());
        Mockito.verify(bookRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookMapper, categoryRepository, bookRepository);
    }

    @Test
    @DisplayName("Verify findAll() method works for BookServiceImpl")
    public void findAllBooks_ValidPageable_ReturnListOfBookDto() {
        //Given
        Book book = validBook();
        BookDto bookDto = validBookDto(book);

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> productPage = new PageImpl<>(books, pageable, books.size());

        Mockito.when(bookRepository.findAllWithCategories(pageable))
                .thenReturn(productPage.getContent());
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        List<BookDto> booksDto = bookService.findAll(pageable);

        //Then
        Assertions.assertEquals(booksDto.size(), books.size());
        Assertions.assertEquals(booksDto.get(0), bookDto);
        Mockito.verify(bookMapper, Mockito.times(books.size())).toDto(Mockito.any());
        Mockito.verify(bookRepository, Mockito.times(1)).findAllWithCategories(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify getById() method works for BookServiceImpl")
    public void getBookById_ValidId_ReturnBookDto() {
        //Given
        Long id = 1L;
        Book book = validBook();
        BookDto bookDto = validBookDto(book);

        Mockito.when(bookRepository.findByIdWithCategories(id)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        BookDto actualBookDto = bookService.getById(id);

        //Then
        Assertions.assertEquals(bookDto, actualBookDto);
        Mockito.verify(bookMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(bookRepository, Mockito.times(1)).findByIdWithCategories(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify the expected getById() exception occurs for BookServiceImpl")
    public void getBookById_InvalidId_ThrowEntityNotFoundException() {
        //Given
        Mockito.when(bookRepository.findByIdWithCategories(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class, () -> bookService.getById(100L));

        //Then
        Assertions.assertEquals("Can't find book by id = 100", exception.getMessage());
        Mockito.verify(bookRepository, Mockito.times(1)).findByIdWithCategories(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify search() method works for BookServiceImpl")
    public void searchBook_ValidSearchParametersDto_ReturnListOfSpecificBookDto() {
        //Given
        Book book = validBook();
        BookDto bookDto = validBookDto(book);
        BookSearchParametersDto searchParametersDto = validAuthorSearch();
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> productPage = new PageImpl<>(books, pageable, books.size());
        Specification<Book> specification = new AuthorSpecificationProvider()
                .getSpecification(searchParametersDto.authors());

        Mockito.when(bookSpecificationBuilder.build(searchParametersDto)).thenReturn(specification);
        Mockito.when(bookRepository.findAll(specification, pageable))
                .thenReturn(productPage);
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        List<BookDto> searchedBooks = bookService.search(searchParametersDto, pageable);

        //Then
        Assertions.assertEquals(searchedBooks.size(), books.size());
        Assertions.assertEquals(searchedBooks.get(0), bookDto);
        Mockito.verify(bookMapper, Mockito.times(books.size())).toDto(Mockito.any());
        Mockito.verify(bookSpecificationBuilder, Mockito.times(1)).build(Mockito.any());
        Mockito.verify(bookRepository, Mockito.times(1))
                .findAll(specification, pageable);
        Mockito.verifyNoMoreInteractions(bookMapper, bookRepository, bookSpecificationBuilder);
    }

    @Test
    @DisplayName("Verify findAllByCategoryId() method works for BookServiceImpl")
    public void findAllByCategoryId_ValidIdAndPageable_ReturnListOfBookDtoWithoutCategoryIds() {
        //Given
        Long id = 1L;
        Book book = validBook();
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = validBookDtoWithoutCategoryIds(book);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> productPage = new PageImpl<>(books, pageable, books.size());

        Mockito.when(bookRepository.findAllByCategoryId(id, pageable))
                .thenReturn(productPage.getContent());
        Mockito.when(bookMapper.toDtoWithoutCategories(book))
                .thenReturn(bookDtoWithoutCategoryIds);

        //When
        List<BookDtoWithoutCategoryIds> searchedBooks =
                bookService.findAllByCategoryId(id, pageable);

        //Then
        Assertions.assertEquals(searchedBooks.size(), books.size());
        Assertions.assertEquals(searchedBooks.get(0), bookDtoWithoutCategoryIds);
        Mockito.verify(bookMapper, Mockito.times(books.size()))
                .toDtoWithoutCategories(Mockito.any());
        Mockito.verify(bookRepository, Mockito.times(1))
                .findAllByCategoryId(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify update() method works for BookServiceImpl")
    public void updateBook_ValidRequestDto_ReturnBookDto() {
        //Given
        Book book = validBook();
        CreateBookRequestDto requestDto = validRequestDto(book);
        BookDto bookDto = validBookDto(book).setCategories(requestDto.categories());

        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(categoryRepository.findAllByIdIn(Mockito.anySet()))
                .thenReturn(book.getCategories());
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        //When
        BookDto updatedBookDto = bookService.update(Mockito.anyLong(), requestDto);

        //Then
        Assertions.assertEquals(bookDto, updatedBookDto);
        Mockito.verify(bookMapper, Mockito.times(1)).toModel(Mockito.any());
        Mockito.verify(bookMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(categoryRepository, Mockito.times(1)).findAllByIdIn(Mockito.any());
        Mockito.verify(bookRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookMapper, categoryRepository, bookRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method works for BookServiceImpl")
    public void deleteById_ValidId() {
        //Given
        Long id = 1L;

        //When
        bookService.deleteById(id);

        //Then
        Mockito.verify(bookRepository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(bookRepository);
    }

    private Book validBook() {
        return new Book()
                .setId(1L)
                .setTitle("valid_test_title")
                .setAuthor("valid_test_author")
                .setIsbn("valid_test_isbn")
                .setPrice(BigDecimal.valueOf(20.20))
                .setDescription("test_description")
                .setCoverImage("test_cover_image")
                .setCategories(validCategories());
    }

    private CreateBookRequestDto validRequestDto(Book book) {
        return new CreateBookRequestDto(
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPrice(),
                book.getDescription(),
                book.getCoverImage(),
                Set.of(1L, 2L)
        );
    }

    private BookDto validBookDto(Book book) {
        return new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());
    }

    private BookSearchParametersDto validAuthorSearch() {
        return new BookSearchParametersDto(
                null,
                new String[]{"valid_test_author"},
                null
        );
    }

    private BookDtoWithoutCategoryIds validBookDtoWithoutCategoryIds(Book book) {
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

    private Set<Category> validCategories() {
        Category firstCategory = new Category().setId(1L);
        Category secondCategory = new Category().setId(2L);
        return Set.of(firstCategory, secondCategory);
    }
}
