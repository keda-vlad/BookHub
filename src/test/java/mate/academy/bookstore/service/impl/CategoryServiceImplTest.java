package mate.academy.bookstore.service.impl;

import static mate.academy.bookstore.util.TestCategoryProvider.toCategoryDto;
import static mate.academy.bookstore.util.TestCategoryProvider.toRequestDto;
import static mate.academy.bookstore.util.TestCategoryProvider.validCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.RequestCategoryDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CategoryMapper;
import mate.academy.bookstore.model.Category;
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

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Verify findAll() method works for CategoryServiceImpl")
    public void findAllCategories_ValidPageable_ReturnListCategoryDto() {
        Category category = validCategory();
        CategoryDto categoryDto = toCategoryDto(category);

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> productPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(productPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> categoriesDto = categoryService.findAll(pageable);

        assertEquals(categoriesDto.size(), categories.size());
        assertEquals(categoriesDto.get(0), categoryDto);
        verify(categoryMapper, times(categories.size())).toDto(any());
        verify(categoryRepository).findAll(pageable);
        verifyNoMoreInteractions(categoryMapper, categoryRepository);

    }

    @Test
    @DisplayName("Verify getById() method works for CategoryServiceImpl")
    public void getCategoryById_ValidId_ReturnCategoryDto() {
        Long id = 1L;
        Category category = validCategory();
        CategoryDto categoryDto = toCategoryDto(category);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actualCategoryDto = categoryService.getById(id);

        assertEquals(categoryDto, actualCategoryDto);
        verify(categoryMapper).toDto(any());
        verify(categoryRepository).findById(any());
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Verify the expected getById() exception occurs for CategoryServiceImpl")
    public void getBookById_InvalidId_ThrowEntityNotFoundException() {
        when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(100L));

        assertEquals("Can't find book by id = 100", exception.getMessage());
        verify(categoryRepository).findById(any());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify save() method works for CategoryServiceImpl")
    public void saveCategory_ValidRequestDto_ReturnCategoryDto() {
        Category category = validCategory();
        RequestCategoryDto requestDto = toRequestDto(category);
        CategoryDto categoryDto = toCategoryDto(category);

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto savedBookDto = categoryService.save(requestDto);

        assertEquals(categoryDto, savedBookDto);
        verify(categoryMapper).toModel(any());
        verify(categoryMapper).toDto(any());
        verify(categoryRepository).save(any());
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Verify update() method works for CategoryServiceImpl")
    public void updateCategory_ValidRequestDto_ReturnCategoryDto() {
        Category category = validCategory();
        RequestCategoryDto requestDto = toRequestDto(category);
        CategoryDto categoryDto = toCategoryDto(category);

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto savedBookDto = categoryService.update(anyLong(), requestDto);

        assertEquals(categoryDto, savedBookDto);
        verify(categoryMapper).toModel(any());
        verify(categoryMapper).toDto(any());
        verify(categoryRepository).save(any());
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method works for CategoryServiceImpl")
    public void deleteById_ValidId() {
        Long id = 1L;
        categoryService.deleteById(id);
        verify(categoryRepository).deleteById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }
}
