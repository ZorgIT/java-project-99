package hexlet.code.app.mapper;

import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LabelMapper {

    LabelDTO map(Label label);

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