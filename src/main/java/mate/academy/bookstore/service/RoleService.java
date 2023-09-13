package mate.academy.bookstore.service;

import mate.academy.bookstore.model.Role;

public interface RoleService {
    Role getByName(Role.RoleName roleName);
}
