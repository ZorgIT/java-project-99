package hexlet.code.app.dto;

import hexlet.code.app.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private String name;
    private Integer index;
    private String description;
    private Long statusId;
    private Long assigneeId;
}
