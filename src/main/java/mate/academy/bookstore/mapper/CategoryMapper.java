package mate.academy.bookstore.mapper;

import mate.academy.bookstore.config.MapperConfig;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.RequestCategoryDto;
import mate.academy.bookstore.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(RequestCategoryDto categoryDto);
}
