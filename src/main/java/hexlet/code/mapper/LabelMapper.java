package hexlet.code.mapper;

import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;


import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LabelMapper {

    LabelDTO map(Label label);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDate.now())")
    Label map(LabelCreateDTO dto);

    default Set<Long> mapTasksToIds(Set<Task> tasks) {
        if (tasks == null) {
            return null;
        }
        return tasks.stream()
                .map(Task::getId)
                .collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(LabelUpdateDTO dto, @MappingTarget Label label);
}
