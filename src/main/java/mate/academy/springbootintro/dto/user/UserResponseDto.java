package mate.academy.springbootintro.dto.user;

import java.util.Set;
import lombok.Data;
import mate.academy.springbootintro.model.Role;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String shippingAddress;
    private Set<Role> roles;
}
