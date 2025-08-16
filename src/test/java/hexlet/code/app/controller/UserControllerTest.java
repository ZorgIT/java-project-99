package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.EmailAlreadyExistsException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.User;
import hexlet.code.app.service.UserService;
import hexlet.code.app.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public UserUtils userUtils() {
            return Mockito.mock(UserUtils.class);
        }

    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;



    private UserDTO createTestUserDTO(Long id, String email, String firstName, String lastName) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setCreatedAt(LocalDate.parse("2023-10-30"));
        return dto;
    }

    private final UserDTO user1 = createTestUserDTO(1L, "john@google.com", "John", "Doe");
    private final UserDTO user2 = createTestUserDTO(2L, "jack@yahoo.com", "Jack", "Jons");

    @Test
    @WithMockUser(username = "john@google.com")
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user1);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@google.com"));
    }

    @Test
    @WithMockUser(username = "john@google.com")
    void getUserById_ShouldReturnNotFound() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "john@google.com")
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("jack@google.com");
        createDTO.setFirstName("Jack");
        createDTO.setLastName("Jons");
        createDTO.setPassword("some-password");

        UserDTO newUserDTO = createTestUserDTO(3L, "jack@google.com", "Jack", "Jons");
        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(newUserDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void createUser_ShouldReturnConflictForDuplicateEmail() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("john@google.com");
        createDTO.setFirstName("John");  // добавить обязательные поля
        createDTO.setLastName("Doe");
        createDTO.setPassword("password");

        when(userService.createUser(any(UserCreateDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists: john@google.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isConflict());
    }

    // --- UPDATE USER ---
    @Test
    @WithMockUser(username = "john@google.com")
    void updateUser_ShouldReturnUpdatedUser_WhenUpdatingSelf() throws Exception {
        // DTO с данными для обновления
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("john@google.com");
        updateDTO.setPassword("new-password");

        // Мок текущего пользователя
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("john@google.com");
        when(userUtils.getCurrentUser()).thenReturn(currentUser);

        // Мок поведения сервиса
        when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(user1);

        // Вызов контроллера через MockMvc
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "john@google.com")
    void updateUser_ShouldReturnForbidden_WhenUpdatingAnotherUser() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("jack@yahoo.com");

        mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "john@google.com")
    void deleteUser_ShouldReturnNoContent_WhenDeletingSelf() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        User testUser = new User();
        testUser.setId(1L);
        when(userUtils.getCurrentUser()).thenReturn(testUser);
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "john@google.com")
    void deleteUser_ShouldReturnForbidden_WhenDeletingAnotherUser() throws Exception {
        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isForbidden());
    }
}

