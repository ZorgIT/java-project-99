package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.exception.EmailAlreadyExistsException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.exception.TaskStatusAlreadyExistsException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class TaskStatusService {
    private final TaskStatusMapper taskStatusMapper;
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskStatusService(TaskStatusRepository taskStatusRepository, TaskStatusMapper taskStatusMapper) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskStatusMapper = taskStatusMapper;
    }

    public TaskStatusDTO getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status"
                        + " not found with id: " + id));
        return taskStatusMapper.map(taskStatus);
    }


    public List<TaskStatusDTO> getAllTaskStatus() {
        log.info("GET /api/task_statuses request received");
        return taskStatusRepository.findAll()
                .stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO createTaskStatus(@Valid TaskStatusCreateDTO taskStatusCreateDTO) {
        if (taskStatusRepository.existsByName(taskStatusCreateDTO.getName())) {
            throw new TaskStatusAlreadyExistsException("Task status already " +
                    "exists: " + taskStatusCreateDTO.getName());
        }
        var taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        var taskStatusSaved = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatusSaved);
    }

    public TaskStatusDTO updateTaskStatus(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        var existingTaskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with id: " + id));

        if (taskStatusUpdateDTO.getName() != null &&
                !existingTaskStatus.getName().equals(taskStatusUpdateDTO.getName()) &&
                taskStatusRepository.existsByName(taskStatusUpdateDTO.getName())) {
            throw new TaskStatusAlreadyExistsException("Task status already exists: " + taskStatusUpdateDTO.getName());
        }

        // 🔧 обновляем поля
        if (taskStatusUpdateDTO.getName() != null) {
            existingTaskStatus.setName(taskStatusUpdateDTO.getName());
        }
        if (taskStatusUpdateDTO.getSlug() != null) {
            existingTaskStatus.setSlug(taskStatusUpdateDTO.getSlug());
        }

        TaskStatus updatedTaskStatus = taskStatusRepository.save(existingTaskStatus);
        return taskStatusMapper.map(updatedTaskStatus);
    }

    public void deleteTaskStatus(Long id) {
        if (!taskStatusRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task status not found with id: " + id);
        }
        taskStatusRepository.deleteById(id);
    }

    public Long getTotalCountTaskStatus() {
        return taskStatusRepository.count();
    }


}
