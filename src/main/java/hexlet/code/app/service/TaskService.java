package hexlet.code.app.service;

import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.exception.TaskStatusAlreadyExistsException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       TaskMapper taskMapper,
                       UserRepository userRepository,
                       TaskStatusRepository taskStatusRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not "
                        + "found with id: " + id));
        return taskMapper.map(task);
    }

    public List<TaskDTO> getAllTask() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO createTask(TaskDTO dto) {
        //TODO вывести проверки в отдельный модуль
        if (taskRepository.existsById(dto.getId())) {
            throw new TaskStatusAlreadyExistsException("Task already exists"
                    + " with id: " + dto.getId());
        }
        if (!userRepository.existsById(dto.getAssignee().getId())) {
            throw new ResourceNotFoundException("User not found with id: "
                    + dto.getAssignee().getId());
        }
        if (!taskStatusRepository.existsById(dto.getStatus().getId())) {
            throw new ResourceNotFoundException("Task status not found with " +
                    "id: "
                    + dto.getAssignee().getId());
        }
        Task task = taskMapper.map(dto);
        return taskMapper.map(taskRepository.save(task));
    }

    public TaskDTO updateTask(Long id, TaskUpdateDTO dto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: "
                        + id));
        if (!userRepository.existsById(dto.getAssignee().getId())) {
            throw new ResourceNotFoundException("User not found with id: "
                    + dto.getAssignee().getId());
        }
        if (!taskStatusRepository.existsById(dto.getStatus().getId())) {
            throw new ResourceNotFoundException("Task status not found with " +
                    "id: "
                    + dto.getAssignee().getId());
        }

        taskMapper.update(dto, existingTask);
        Task updatedTask = taskRepository.save(existingTask);

        return taskMapper.map(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: "
                    + id);
        }

        taskRepository.deleteById(id);
    }


}
