package hexlet.code.app.component;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.utils.RandomUsers;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("!test")
public class DataInitializer implements ApplicationRunner {


    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;


    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private TaskStatusRepository taskStatusRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        var userData = new UserCreateDTO();
        userData.setLastName("admin");
        userData.setFirstName("admin");
        userData.setEmail("hexlet@example.com");
        userData.setPassword(passwordEncoder.encode("qwerty"));
        var user = userMapper.map(userData);
        userRepository.save(user);

        var randomUsers = RandomUsers.generateFakeUsers(5);
        userRepository.saveAll(randomUsers);

        taskStatusRepository.save(TaskStatus.of("draft"));
        taskStatusRepository.save(TaskStatus.of("to review"));
        taskStatusRepository.save(TaskStatus.of("to be fixed"));
        taskStatusRepository.save(TaskStatus.of("to publish"));
        taskStatusRepository.save(TaskStatus.of("published"));
    }
}