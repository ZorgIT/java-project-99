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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    /*@Test
    void getTaskById_ShouldReturnTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(testTask);

        mockMvc.perform(get("/api/tasks/1")
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test task"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.assigneeId").value(1))
                .andExpect(jsonPath("$.createdAt").value("2023-10-30"));
    }*/

    @Test
    void getAllTasks_ShouldReturnList() throws Exception {
        TaskDTO anotherTask = createTestTask(2L, "Another task", "Desc", "to_be_fixed", 1L);

        when(taskService.getAllTasks()).thenReturn(List.of(testTask, anotherTask));

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test task"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Another task"));
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
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
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
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
    void deleteTask_ShouldReturnNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1")
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isNoContent());
    }

    @Test
    void filterTasks_ShouldReturnFilteredList() throws Exception {
        when(taskService.getTasks("create", 1L, "to_be_fixed", 1L)).thenReturn(List.of(testTask));

        mockMvc.perform(get("/api/tasks")
                        .param("titleCont", "create")
                        .param("assigneeId", "1")
                        .param("status", "to_be_fixed")
                        .param("labelId", "1")
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test task"));
    }
}
