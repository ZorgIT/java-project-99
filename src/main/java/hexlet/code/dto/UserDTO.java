package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private long id;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}
