package hexlet.code.app.mapper;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.model.User;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    UserDTO map(User model);

    @Mapping(target = "tasks", ignore = true) // tasks есть только в entity
    User map(UserCreateDTO dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    User map(UserDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    void update(UserUpdateDTO dto, @MappingTarget User model);
}

