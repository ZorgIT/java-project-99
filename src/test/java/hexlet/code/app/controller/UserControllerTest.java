package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.EmailAlreadyExistsException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private UserDTO createTestUserDTO(Long id, String email, String firstName, String lastName) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setCreatedAt(LocalDate.parse("2023-10-30"));
        return dto;
    }

    private final UserDTO testUserDTO = createTestUserDTO(1L, "john@google.com", "John", "Doe");

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@google.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.createdAt").value("2023-10-30"));
    }

    @Test
    void getUserById_ShouldReturnNotFound() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        UserDTO secondUserDTO = createTestUserDTO(2L, "jack@yahoo.com", "Jack", "Jons");

        when(userService.getAllUsers()).thenReturn(List.of(testUserDTO, secondUserDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("john@google.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("jack@yahoo.com"));
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
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("jack@google.com"))
                .andExpect(jsonPath("$.firstName").value("Jack"))
                .andExpect(jsonPath("$.lastName").value("Jons"))
                .andExpect(jsonPath("$.createdAt").value("2023-10-30"));
    }

    @Test
    void createUser_ShouldReturnConflictForDuplicateEmail() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("john@google.com");
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setPassword("password");

        when(userService.createUser(any(UserCreateDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists: john@google.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isConflict());
    }


    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("jack@yahoo.com");
        updateDTO.setPassword("new-password");

        UserDTO updatedUserDTO = createTestUserDTO(3L, "jack@yahoo.com", "Jack", "Jons");

        when(userService.updateUser(eq(3L), any(UserUpdateDTO.class))).thenReturn(updatedUserDTO);

        mockMvc.perform(put("/api/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("jack@yahoo.com"))
                .andExpect(jsonPath("$.firstName").value("Jack"))
                .andExpect(jsonPath("$.lastName").value("Jons"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found with id: 99"))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
