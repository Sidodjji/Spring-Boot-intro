package mate.academy.springbootintro.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.user.UserRegistrationRequestDto;
import mate.academy.springbootintro.dto.user.UserResponseDto;
import mate.academy.springbootintro.exeption.RegistrationException;
import mate.academy.springbootintro.mapper.UserMapper;
import mate.academy.springbootintro.model.Role;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.repository.RoleRepository;
import mate.academy.springbootintro.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toModel(requestDto);
        Role userRole = roleRepository.findByRole(Role.RoleName.USER).orElseThrow(
                () -> new RuntimeException("Role USER not found"));
        user.setRoles(Set.of(userRole));
        return userMapper.toDto(userRepository.save(user));
    }
}
