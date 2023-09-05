package mate.academy.bookstore.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.BookSearchParametersDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilderImpl implements BookSpecificationBuilder {
    private static final String TITLE_KEY = "title";
    private static final String AUTHOR_KEY = "author";
    private static final String PRICE_KEY = "price";
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto bookSearchParametersDto) {
        Specification<Book> spec = Specification.where(null);
        if (bookSearchParametersDto.getTitles() != null
                && bookSearchParametersDto.getTitles().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(TITLE_KEY)
                    .getSpecification(bookSearchParametersDto.getTitles()));
        }
        if (bookSearchParametersDto.getAuthors() != null
                && bookSearchParametersDto.getAuthors().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(AUTHOR_KEY)
                    .getSpecification(bookSearchParametersDto.getAuthors()));

        }
        if (bookSearchParametersDto.getPrices() != null
                && bookSearchParametersDto.getPrices().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(PRICE_KEY)
                    .getSpecification(bookSearchParametersDto.getPrices()));

        }
        return spec;
    }
}
