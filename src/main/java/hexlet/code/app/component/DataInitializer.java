package hexlet.code.app.component;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.LabelService;
import hexlet.code.app.service.TaskService;
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
    @Autowired
    private TaskService taskService;
    @Autowired
    private LabelService labelService;


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

        var draftStatus = taskStatusRepository.save(TaskStatus.of("draft"));
        var toReviewStatus = taskStatusRepository.save(TaskStatus.of("to review"));
        var toBeFixedStatus = taskStatusRepository.save(TaskStatus.of("to be fixed"));
        var toPublishStatus = taskStatusRepository.save(TaskStatus.of("to publish"));
        var publishedStatus = taskStatusRepository.save(TaskStatus.of("published"));

        var label1 = new LabelCreateDTO();
        label1.setName("Bug");

        var label2 = new LabelCreateDTO();
        label2.setName("Feature");

        var label3 = new LabelCreateDTO();
        label3.setName("Improvement");

        labelService.createLabel(label1);
        labelService.createLabel(label2);
        labelService.createLabel(label3);


        TaskCreateDTO task1 = new TaskCreateDTO();
        task1.setTitle("task1");
        task1.setContent("task description");
        task1.setStatus(draftStatus.getSlug()); // <-- статус
        taskService.createTask(task1);

        TaskCreateDTO task2 = new TaskCreateDTO();
        task2.setTitle("task2");
        task2.setContent("task description");
        task2.setStatus(toReviewStatus.getSlug()); // <-- статус
        task2.setIndex(10);
        taskService.createTask(task2);
    }
}