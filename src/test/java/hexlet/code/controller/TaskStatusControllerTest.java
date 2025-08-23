package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskStatusService;
import hexlet.code.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TaskStatusControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock
    private TaskStatusService taskStatusService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TaskStatusController taskStatusController;


    private TaskStatusDTO draftStatus;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(taskStatusController).build();

        // Create test status
        draftStatus = createTestStatus(1L, "Draft", "draft");

        // Mock user creation
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

    private TaskStatusDTO createTestStatus(Long id, String name, String slug) {
        TaskStatusDTO dto = new TaskStatusDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setSlug(slug);
        dto.setCreatedAt(LocalDate.of(2023, 10, 30));
        return dto;
    }

    @Test
    void getTaskStatusByIdShouldReturnStatus() throws Exception {
        when(taskStatusService.getTaskStatusById(1L)).thenReturn(draftStatus);

        mockMvc.perform(get("/api/task_statuses/1")
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Draft"))
                .andExpect(jsonPath("$.slug").value("draft"))
                .andExpect(jsonPath("$.createdAt").value("2023-10-30"));
    }


    @Test
    void getAllTaskStatusesShouldReturnList() throws Exception {
        TaskStatusDTO reviewStatus = createTestStatus(2L, "ToReview", "to_review");

        when(taskStatusService.getAllTaskStatuses()).thenReturn(List.of(draftStatus, reviewStatus));

        mockMvc.perform(get("/api/task_statuses")
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Draft"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("ToReview"));
    }

    @Test
    void createTaskStatusShouldReturnCreatedStatus() throws Exception {
        TaskStatusCreateDTO createDTO = new TaskStatusCreateDTO();
        createDTO.setName("New");
        createDTO.setSlug("new");

        TaskStatusDTO created = createTestStatus(3L, "New", "new");

        when(taskStatusService.createTaskStatus(any(TaskStatusCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.slug").value("new"))
                .andExpect(jsonPath("$.createdAt").value("2023-10-30"));
    }

    @Test
    void updateTaskStatusShouldReturnUpdatedStatus() throws Exception {
        TaskStatusUpdateDTO updateDTO = new TaskStatusUpdateDTO();
        updateDTO.setName("UpdatedStatus");

        TaskStatusDTO updated = createTestStatus(1L, "UpdatedStatus", "draft");

        when(taskStatusService.updateTaskStatus(eq(1L), any(TaskStatusUpdateDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/task_statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("UpdatedStatus"))
                .andExpect(jsonPath("$.slug").value("draft"));
    }

    @Test
    void deleteTaskStatusShouldReturnNoContent() throws Exception {
        doNothing().when(taskStatusService).deleteTaskStatus(1L);

        mockMvc.perform(delete("/api/task_statuses/1")
                        .header("Authorization", "Basic " + "hexlet@example.com:qwerty"))
                .andExpect(status().isNoContent());
    }
}
