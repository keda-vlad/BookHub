package mate.academy.bookstore.repository.category;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookstore.model.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Verify findAllByCategoryId() method works for CategoryRepository")
    @Sql(scripts = {
            "classpath:database/category/add-three-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/category/remove-three-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidCategoryIdAndPageable_ReturnBookList() {
        Set<Category> actual = categoryRepository.findAllByIdIn(List.of(1L, 2L));
        Assertions.assertEquals(2, actual.size());
        actual = actual.stream()
                .filter(c -> c.getName().equals("Fantasy Adventure")
                        || c.getName().equals("Philosophy"))
                .collect(Collectors.toSet());
        Assertions.assertEquals(2, actual.size());
    }
}
