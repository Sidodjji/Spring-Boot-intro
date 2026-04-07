package mate.academy.springbootintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.user.UserLoginRequestDto;
import mate.academy.springbootintro.dto.user.UserLoginResponseDto;
import mate.academy.springbootintro.dto.user.UserRegistrationRequestDto;
import mate.academy.springbootintro.dto.user.UserResponseDto;
import mate.academy.springbootintro.exeption.RegistrationException;
import mate.academy.springbootintro.security.AuthenticationService;
import mate.academy.springbootintro.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication management", description = "Endpoints for user authentication")
public class AuthenticationController {
    private final UserService userService;

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register new user", description = "Create a new user account")
    @PostMapping("/registration")
    public UserResponseDto register(@Valid @RequestBody UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
