package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateDTO {
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot be longer than 50 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 3, message = "Password must be at least 3 characters")
    private String password;
}
