package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.service.TaskService;
import hexlet.code.app.dto.UserCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TaskControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock
    private TaskService taskService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TaskController taskController;

    private TaskDTO testTask;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        // Создаём тестовую задачу
        testTask = createTestTask(1L, "Test task", "Some description", "draft", 1L);

        // Создаём тестового пользователя (как в твоём примере)
        UserCreateDTO userData = new UserCreateDTO();
        userData.setFirstName("admin");
        userData.setLastName("admin");
        userData.setEmail("hexlet@example.com");
        userData.setPassword("qwerty");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName(userData.getFirstName());
        testUser.setLastName(userData.getLastName());
        testUser.setEmail(userData.getEmail());
        testUser.setPassword("encodedPassword");

        when(passwordEncoder.encode("qwerty")).thenReturn("encodedPassword");
        when(userMapper.map(userData)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userRepository.findByEmail("hexlet@example.com")).thenReturn(java.util.Optional.of(testUser));
    }

    private TaskDTO createTestTask(Long id, String title, String content, String status, Long assigneeId) {
        TaskDTO dto = new TaskDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setStatus(status);
        dto.setAssigneeId(assigneeId);
        dto.setCreatedAt(LocalDate.of(2023, 10, 30));
        return dto;
    }



    @Test
    void createTaskShouldReturnCreatedTask() throws Exception {
        TaskCreateDTO createDTO = new TaskCreateDTO();
        createDTO.setTitle("New task");
        createDTO.setContent("New description");

        TaskDTO created = createTestTask(3L, "New task", "New description", "draft", 1L);

        when(taskService.createTask(any(TaskCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("New task"))
                .andExpect(jsonPath("$.status").value("draft"));
    }

    @Test
    void updateTaskShouldReturnUpdatedTask() throws Exception {
        TaskUpdateDTO updateDTO = new TaskUpdateDTO();
        updateDTO.setTitle("Updated title");

        TaskDTO updated = createTestTask(1L, "Updated title", "Some description", "draft", 1L);

        when(taskService.updateTask(eq(1L), any(TaskUpdateDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated title"));
    }

    @Test
    void deleteTaskShouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1")
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isNoContent());
    }

}
