package mate.academy.bookstore.repository.book.spec;

import java.math.BigDecimal;
import java.util.Arrays;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "price";

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(KEY).in(Arrays.stream(params)
                .map(BigDecimal::new)
                .toArray());
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
