package hexlet.code.app.dto;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;

public class TaskUpdateDTO {
    private String name;
    private Integer index;
    private String description;
    private TaskStatus status;
    private User assignee;
}
