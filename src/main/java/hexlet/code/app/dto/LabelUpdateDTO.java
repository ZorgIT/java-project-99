package hexlet.code.app.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class LabelUpdateDTO {
    @NotBlank
    @Size(min = 3, max = 1000)
    private String name;
}
