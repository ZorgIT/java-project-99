package hexlet.code.mapper;


import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;

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

    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "id", ignore = true)
    void update(TaskStatusUpdateDTO dto, @MappingTarget TaskStatus model);
}
