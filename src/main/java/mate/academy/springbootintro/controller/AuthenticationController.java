package mate.academy.springbootintro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.user.UserRegistrationRequestDto;
import mate.academy.springbootintro.dto.user.UserResponseDto;
import mate.academy.springbootintro.exeption.RegistrationException;
import mate.academy.springbootintro.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/registration")
    public UserResponseDto register(@Valid @RequestBody UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }
}
