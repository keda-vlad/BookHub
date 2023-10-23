package mate.academy.bookstore.repository.user;

import static mate.academy.bookstore.util.TestUserProvider.validUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.bookstore.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Verify findByEmail() method works for UserRepository")
    @Sql(scripts = {
            "classpath:database/user/create-user-for-find-by-email.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/user/remove-user-for-find-by-email.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_ValidEmail_returnOptionalUser() {
        User expected = validUser();

        Optional<User> optionalUser = userRepository.findByEmail("somenew@example.com");

        assertTrue(optionalUser.isPresent());
        assertEquals(expected, optionalUser.get());
    }
}
