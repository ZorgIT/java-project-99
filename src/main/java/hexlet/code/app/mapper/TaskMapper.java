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
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    TaskDTO map(Task model);

    Task map(TaskDTO dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task map(TaskCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", ignore = true)   // обновляем вручную в сервисе
    @Mapping(target = "assignee", ignore = true)
        // обновляем вручную в сервисе
    void update(TaskUpdateDTO dto, @MappingTarget Task model);

}
