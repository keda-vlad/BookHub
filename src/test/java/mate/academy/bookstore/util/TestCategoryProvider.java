package mate.academy.bookstore.util;

import java.util.List;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.RequestCategoryDto;
import mate.academy.bookstore.model.Category;

public class TestCategoryProvider {
    public static RequestCategoryDto toRequestDto(Category category) {
        return new RequestCategoryDto(
                category.getName(),
                category.getDescription()
        );
    }

    public static Category validCategory() {
        return new Category()
                .setId(1L)
                .setName("test_category_name")
                .setDescription("test_description");
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    public static List<Category> threeCategoriesFromDb() {
        return List.of(
                new Category()
                        .setId(1L)
                        .setName("Fantasy Adventure")
                        .setDescription("Fantasy Adventure"),
                new Category()
                        .setId(2L)
                        .setName("Philosophy")
                        .setDescription("Philosophy"),
                new Category()
                        .setId(3L)
                        .setName("Social science fiction")
                        .setDescription("Social science fiction")
        );
    }

    public static Category validCategoryForCreate() {
        return new Category()
                .setName("test_name")
                .setDescription("test_description");
    }

    public static RequestCategoryDto updateRequestDto() {
        return new RequestCategoryDto(
                "newName",
                "newDescription"
        );
    }

    public static CategoryDto updateResponseDto(RequestCategoryDto requestDto) {
        Category updatedCategory = new Category()
                .setId(7L)
                .setDescription(requestDto.description())
                .setName(requestDto.name());
        return toCategoryDto(updatedCategory);
    }
}
