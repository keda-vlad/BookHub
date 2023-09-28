package mate.academy.bookstore.service.impl;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.RequestCategoryDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CategoryMapper;
import mate.academy.bookstore.model.Category;
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
        //Given
        Category category = validCategory();
        CategoryDto categoryDto = validCategoryDto(category);

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> productPage = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(productPage);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //When
        List<CategoryDto> categoriesDto = categoryService.findAll(pageable);

        //Then
        Assertions.assertEquals(categoriesDto.size(), categories.size());
        Assertions.assertEquals(categoriesDto.get(0), categoryDto);
        Mockito.verify(categoryMapper, Mockito.times(categories.size())).toDto(Mockito.any());
        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verifyNoMoreInteractions(categoryMapper, categoryRepository);

    }

    @Test
    @DisplayName("Verify getById() method works for CategoryServiceImpl")
    public void getCategoryById_ValidId_ReturnCategoryDto() {
        //Given
        Long id = 1L;
        Category category = validCategory();
        CategoryDto categoryDto = validCategoryDto(category);

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //When
        CategoryDto actualCategoryDto = categoryService.getById(id);

        //Then
        Assertions.assertEquals(categoryDto, actualCategoryDto);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Verify the expected getById() exception occurs for CategoryServiceImpl")
    public void getBookById_InvalidId_ThrowEntityNotFoundException() {
        //Given
        Mockito.when(categoryRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(100L));

        //Then
        Assertions.assertEquals("Can't find book by id = 100", exception.getMessage());
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(Mockito.any());
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify save() method works for CategoryServiceImpl")
    public void saveCategory_ValidRequestDto_ReturnCategoryDto() {
        //Given
        Category category = validCategory();
        RequestCategoryDto requestDto = validRequestDto(category);
        CategoryDto categoryDto = validCategoryDto(category);

        Mockito.when(categoryMapper.toModel(requestDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //When
        CategoryDto savedBookDto = categoryService.save(requestDto);

        //Then
        Assertions.assertEquals(categoryDto, savedBookDto);
        Mockito.verify(categoryMapper, Mockito.times(1)).toModel(Mockito.any());
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Verify update() method works for CategoryServiceImpl")
    public void updateCategory_ValidRequestDto_ReturnCategoryDto() {
        //Given
        Category category = validCategory();
        RequestCategoryDto requestDto = validRequestDto(category);
        CategoryDto categoryDto = validCategoryDto(category);

        Mockito.when(categoryMapper.toModel(requestDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //When
        CategoryDto savedBookDto = categoryService.update(Mockito.anyLong(), requestDto);

        //Then
        Assertions.assertEquals(categoryDto, savedBookDto);
        Mockito.verify(categoryMapper, Mockito.times(1)).toModel(Mockito.any());
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method works for CategoryServiceImpl")
    public void deleteById_ValidId() {
        //Given
        Long id = 1L;

        //When
        categoryService.deleteById(id);

        //Then
        Mockito.verify(categoryRepository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    private RequestCategoryDto validRequestDto(Category category) {
        return new RequestCategoryDto(
                category.getName(),
                category.getDescription()
        );
    }

    private Category validCategory() {
        return new Category()
                .setId(1L)
                .setName("test_category_name")
                .setDescription("test_description");
    }

    private CategoryDto validCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
