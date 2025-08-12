package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private User createTestUser(Long id, String email, String firstName, String lastName, String password) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setCreatedAt(LocalDateTime.parse("2023-10-30T00:00:00"));
        user.setUpdatedAt(LocalDateTime.parse("2023-10-30T00:00:00"));
        return user;
    }

    private UserDTO createTestUserDTO(Long id, String email, String firstName, String lastName) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setCreatedAt(LocalDate.parse("2023-10-30"));
        return dto;
    }

    private final User testUser = createTestUser(1L, "john@google.com", "John", "Doe", "password");
    private final UserDTO testUserDTO = createTestUserDTO(1L, "john@google.com", "John", "Doe");

    @Test
    public void getUserById_ShouldReturnUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.map(testUser)).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@google.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.createdAt").value("2023-10-30"));
    }

    @Test
    public void getUserById_ShouldReturnNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllUsers_ShouldReturnUsersList() throws Exception {
        User secondUser = createTestUser(2L, "jack@yahoo.com", "Jack", "Jons", "password");
        UserDTO secondUserDTO = createTestUserDTO(2L, "jack@yahoo.com", "Jack", "Jons");

        when(userRepository.findAll()).thenReturn(List.of(testUser, secondUser));
        when(userMapper.map(testUser)).thenReturn(testUserDTO);
        when(userMapper.map(secondUser)).thenReturn(secondUserDTO);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("john@google.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("jack@yahoo.com"));
    }

    @Test
    public void createUser_ShouldReturnCreatedUser() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("jack@google.com");
        createDTO.setFirstName("Jack");
        createDTO.setLastName("Jons");
        createDTO.setPassword("some-password");

        User newUser = createTestUser(3L, "jack@google.com", "Jack", "Jons", "some-password");
        UserDTO newUserDTO = createTestUserDTO(3L, "jack@google.com", "Jack", "Jons");

        when(userMapper.map(createDTO)).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(userMapper.map(newUser)).thenReturn(newUserDTO);
        when(userRepository.existsByEmail("jack@google.com")).thenReturn(false);

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
    public void createUser_ShouldReturnConflictForDuplicateEmail() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("john@google.com");
        createDTO.setFirstName("John");
        createDTO.setLastName("Doe");
        createDTO.setPassword("password");

        when(userRepository.existsByEmail("john@google.com")).thenReturn(true);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    public void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("jack@yahoo.com");
        updateDTO.setPassword("new-password");

        User updatedUser = createTestUser(3L, "jack@yahoo.com", "Jack", "Jons", "new-password");
        UserDTO updatedUserDTO = createTestUserDTO(3L, "jack@yahoo.com", "Jack", "Jons");

        when(userRepository.findById(3L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("jack@yahoo.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.map(updatedUser)).thenReturn(updatedUserDTO);

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
    public void deleteUser_ShouldReturnNoContent() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteUser_ShouldReturnNotFound() throws Exception {
        when(userRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}