package mate.academy.springbootintro.mapper;

import mate.academy.springbootintro.config.MapperConfig;
import mate.academy.springbootintro.dto.user.UserRegistrationRequestDto;
import mate.academy.springbootintro.dto.user.UserResponseDto;
import mate.academy.springbootintro.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    void updateUserFromDto(UserRegistrationRequestDto dto, @MappingTarget User user);
}
