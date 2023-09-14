package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.RequestCategoryDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(RequestCategoryDto categoryDto);

    CategoryDto update(Long id, RequestCategoryDto categoryDto);

    void deleteById(Long id);
}
