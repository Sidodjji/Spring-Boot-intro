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
import mate.academy.springbootintro.service.shoppingcart.ShoppingCartService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final ShoppingCartService shoppingCartService;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role userRole = roleRepository.findByRole(Role.RoleName.USER).orElseThrow(
                () -> new RuntimeException("Role USER not found"));
        user.setRoles(Set.of(userRole));
        User savedUser = userRepository.save(user);
        shoppingCartService.createNewCart(savedUser);
        return userMapper.toDto(savedUser);
    }
}
