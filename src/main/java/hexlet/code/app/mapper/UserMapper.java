package hexlet.code.app.mapper;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.model.User;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(
        nullValuePropertyMappingStrategy =
                NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface UserMapper {

    User map(UserCreateDTO dto);

    UserDTO map(User model);

    User map(UserDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UserUpdateDTO dto, @MappingTarget User model);

}
