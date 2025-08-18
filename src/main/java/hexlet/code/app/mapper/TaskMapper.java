package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;
import org.mapstruct.*;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TaskMapper {

    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "status.name", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    TaskDTO map(Task model);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task map(TaskCreateDTO dto);

    // Обновление существующей задачи
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    void update(TaskUpdateDTO dto, @MappingTarget Task model);
}
