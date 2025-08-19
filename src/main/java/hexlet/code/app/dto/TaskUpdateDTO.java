package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateDTO {
    private String title;
    private Integer index;
    private String content;
    private Long statusId;
    private Long assigneeId;
    private Long labelId;
}
