package mate.academy.bookstore.repository.role;

import mate.academy.bookstore.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Verify findByEmail() method works for RoleRepository")
    void getByName_ValidRoleName_returnRole() {
        Role expected = new Role();
        expected.setId(1L);
        expected.setName(Role.RoleName.ROLE_ADMIN);
        Role actual = roleRepository.getByName(Role.RoleName.ROLE_ADMIN);
        Assertions.assertEquals(expected, actual);
    }
}
