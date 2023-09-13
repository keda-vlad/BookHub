package mate.academy.bookstore.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.repository.role.RoleRepository;
import mate.academy.bookstore.service.RoleService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role getByName(Role.RoleName roleName) {
        return roleRepository.getByName(roleName);
    }
}
