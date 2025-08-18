package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateDTO {
    private String name;
    private Integer index;
    private String description;
    private Long statusId;
    private Long assigneeId;
}
