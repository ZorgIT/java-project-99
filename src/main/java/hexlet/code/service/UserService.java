package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.EmailAlreadyExistsException;
import hexlet.code.exception.ForbiddenException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.UserUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserUtils userUtils;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userUtils = userUtils;
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "
                        + id));
        return userMapper.map(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: "
                    + userCreateDTO.getEmail());
        }
        User user = userMapper.map(userCreateDTO);
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.map(savedUser);
    }

    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "
                        + id));

        if (userUpdateDTO.getEmail() != null
                && !existingUser.getEmail().equals(userUpdateDTO.getEmail())
                && userRepository.existsByEmailAndIdNot(userUpdateDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email already exists: " + userUpdateDTO.getEmail());
        }

        userMapper.update(userUpdateDTO, existingUser);

        if (userUpdateDTO.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.map(updatedUser);
    }

    public void deleteUser(Long id) {
        User currentUser = userUtils.getCurrentUser();
        if (!currentUser.getId().equals(id)) {
            throw new ForbiddenException("You can delete only yourself");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getTasks().isEmpty()) {
            throw new ForbiddenException("Cannot delete user assigned to tasks");
        }

        userRepository.delete(user);
    }


    public long getTotalCount() {
        return userRepository.count();
    }
}
