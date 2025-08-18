package hexlet.code.app.mapper;


import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.model.TaskStatus;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskStatusMapper {


    TaskStatusDTO map(TaskStatus model);

    @Mapping(target = "tasks", ignore = true)
    TaskStatus map(TaskStatusCreateDTO dto);

    @Mapping(target = "tasks", ignore = true)
    TaskStatus map(TaskStatusDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tasks", ignore = true)
    void update(TaskStatusUpdateDTO dto, @MappingTarget TaskStatus model);
}
