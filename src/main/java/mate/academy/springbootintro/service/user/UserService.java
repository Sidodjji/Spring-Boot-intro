package mate.academy.springbootintro.service.user;

import mate.academy.springbootintro.dto.user.UserRegistrationRequestDto;
import mate.academy.springbootintro.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);
}
