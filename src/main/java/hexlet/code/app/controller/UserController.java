package hexlet.code.app.controller;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return ResponseEntity.ok(userMapper.map(user));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists: " + userCreateDTO.getEmail());
        }

        User user = userMapper.map(userCreateDTO);
        User savedUser = userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.map(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));

        if (!existingUser.getEmail().equals(userUpdateDTO.getEmail()) &&
                userRepository.existsByEmail(userUpdateDTO.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists: " + userUpdateDTO.getEmail());
        }

        userMapper.update(userUpdateDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return ResponseEntity.ok(userMapper.map(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
