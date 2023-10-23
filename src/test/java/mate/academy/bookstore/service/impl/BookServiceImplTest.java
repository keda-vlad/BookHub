package mate.academy.bookstore.service.impl;

import static mate.academy.bookstore.util.TestBookProvider.toAuthorSearch;
import static mate.academy.bookstore.util.TestBookProvider.toBookDto;
import static mate.academy.bookstore.util.TestBookProvider.toBookDtoWithoutCategoryIds;
import static mate.academy.bookstore.util.TestBookProvider.toCreateBookRequestDto;
import static mate.academy.bookstore.util.TestBookProvider.validBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.bookstore.dto.book.BookSearchParametersDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.book.BookSpecificationBuilder;
import mate.academy.bookstore.repository.book.spec.AuthorSpecificationProvider;
import mate.academy.bookstore.repository.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        Book book = validBook();
        CreateBookRequestDto requestDto = toCreateBookRequestDto(book);
        BookDto bookDto = toBookDto(book).setCategories(requestDto.categories());

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(categoryRepository.findAllByIdIn(anySet()))
                .thenReturn(book.getCategories());
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBookDto = bookService.save(requestDto);

        assertEquals(bookDto, savedBookDto);
        verify(bookMapper, times(1)).toModel(any());
        verify(bookMapper, times(1)).toDto(any());
        verify(categoryRepository, times(1)).findAllByIdIn(any());
        verify(bookRepository, times(1)).save(any());
        verifyNoMoreInteractions(bookMapper, categoryRepository, bookRepository);
    }

    @Test
    @DisplayName("Verify findAll() method works for BookServiceImpl")
    public void findAllBooks_ValidPageable_ReturnListOfBookDto() {
        Book book = validBook();
        BookDto bookDto = toBookDto(book);

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> productPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllWithCategories(pageable))
                .thenReturn(productPage.getContent());
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> booksDto = bookService.findAll(pageable);

        assertEquals(booksDto.size(), books.size());
        assertEquals(booksDto.get(0), bookDto);
        verify(bookMapper, times(books.size())).toDto(any());
        verify(bookRepository, times(1)).findAllWithCategories(any());
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify getById() method works for BookServiceImpl")
    public void getBookById_ValidId_ReturnBookDto() {
        Long id = 1L;
        Book book = validBook();
        BookDto bookDto = toBookDto(book);

        when(bookRepository.findByIdWithCategories(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actualBookDto = bookService.getById(id);

        assertEquals(bookDto, actualBookDto);
        verify(bookMapper, times(1)).toDto(any());
        verify(bookRepository, times(1)).findByIdWithCategories(any());
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify the expected getById() exception occurs for BookServiceImpl")
    public void getBookById_InvalidId_ThrowEntityNotFoundException() {
        when(bookRepository.findByIdWithCategories(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> bookService.getById(100L));

        assertEquals("Can't find book by id = 100", exception.getMessage());
        verify(bookRepository, times(1)).findByIdWithCategories(any());
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify search() method works for BookServiceImpl")
    public void searchBook_ValidSearchParametersDto_ReturnListOfSpecificBookDto() {
        Book book = validBook();
        BookDto bookDto = toBookDto(book);
        BookSearchParametersDto searchParametersDto = toAuthorSearch(book);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> productPage = new PageImpl<>(books, pageable, books.size());
        Specification<Book> specification = new AuthorSpecificationProvider()
                .getSpecification(searchParametersDto.authors());

        when(bookSpecificationBuilder.build(searchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable))
                .thenReturn(productPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> searchedBooks = bookService.search(searchParametersDto, pageable);

        assertEquals(searchedBooks.size(), books.size());
        assertEquals(searchedBooks.get(0), bookDto);
        verify(bookMapper, times(books.size())).toDto(any());
        verify(bookSpecificationBuilder, times(1)).build(any());
        verify(bookRepository, times(1))
                .findAll(specification, pageable);
        verifyNoMoreInteractions(bookMapper, bookRepository, bookSpecificationBuilder);
    }

    @Test
    @DisplayName("Verify findAllByCategoryId() method works for BookServiceImpl")
    public void findAllByCategoryId_ValidIdAndPageable_ReturnListOfBookDtoWithoutCategoryIds() {
        Long id = 1L;
        Book book = validBook();
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = toBookDtoWithoutCategoryIds(book);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> productPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllByCategoryId(id, pageable))
                .thenReturn(productPage.getContent());
        when(bookMapper.toDtoWithoutCategories(book))
                .thenReturn(bookDtoWithoutCategoryIds);

        List<BookDtoWithoutCategoryIds> searchedBooks =
                bookService.findAllByCategoryId(id, pageable);

        assertEquals(searchedBooks.size(), books.size());
        assertEquals(searchedBooks.get(0), bookDtoWithoutCategoryIds);
        verify(bookMapper, times(books.size()))
                .toDtoWithoutCategories(any());
        verify(bookRepository, times(1))
                .findAllByCategoryId(any(), any());
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify update() method works for BookServiceImpl")
    public void updateBook_ValidRequestDto_ReturnBookDto() {
        Book book = validBook();
        CreateBookRequestDto requestDto = toCreateBookRequestDto(book);
        BookDto bookDto = toBookDto(book).setCategories(requestDto.categories());

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(categoryRepository.findAllByIdIn(anySet()))
                .thenReturn(book.getCategories());
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto updatedBookDto = bookService.update(anyLong(), requestDto);

        assertEquals(bookDto, updatedBookDto);
        verify(bookMapper, times(1)).toModel(any());
        verify(bookMapper, times(1)).toDto(any());
        verify(categoryRepository, times(1)).findAllByIdIn(any());
        verify(bookRepository, times(1)).save(any());
        verifyNoMoreInteractions(bookMapper, categoryRepository, bookRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method works for BookServiceImpl")
    public void deleteById_ValidId() {
        Long id = 1L;
        bookService.deleteById(id);
        verify(bookRepository, times(1))
                .deleteById(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }
}
