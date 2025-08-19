package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Integer index;
    private LocalDate createdAt;
    private String title;
    private String content;
    private String status;
    private Long assigneeId;
    private Set<Long> labelIds;
}
