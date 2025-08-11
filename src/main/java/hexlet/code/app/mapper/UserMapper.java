package hexlet.code.app.mapper;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.model.User;
import org.mapstruct.*;

@Mapper(
        nullValuePropertyMappingStrategy =
                NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class UserMapper {

    public abstract User map(UserCreateDTO dto);

    public abstract UserDTO map(User model);

    public abstract User map(UserDTO dto);

    public abstract void update(UserCreateDTO dto, @MappingTarget User model);

}
