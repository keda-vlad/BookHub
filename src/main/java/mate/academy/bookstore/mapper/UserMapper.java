package mate.academy.bookstore.mapper;

import mate.academy.bookstore.config.MapperConfig;
import mate.academy.bookstore.dto.user.UserRegistrationResponseDto;
import mate.academy.bookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toResponseDto(User user);
}
