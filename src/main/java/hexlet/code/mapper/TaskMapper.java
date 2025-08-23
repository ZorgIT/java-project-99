package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.Label;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;

import java.util.stream.Collectors;


@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TaskMapper {

    @Mapping(source = "status.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    TaskDTO map(Task task);

    @AfterMapping
    default void setLabelsId(Task task, @MappingTarget TaskDTO dto) {
        if (task.getLabels() != null) {
            dto.setLabelsId(task.getLabels().stream()
                    .map(Label::getId)
                    .collect(Collectors.toSet()));
        }
    }

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(target = "status", ignore = true)   // обработать вручную
    @Mapping(target = "assignee", ignore = true) // обработать вручную
    @Mapping(target = "labels", ignore = true)
        // обработать вручную
    Task map(TaskCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    void update(TaskUpdateDTO dto, @MappingTarget Task task);
}
