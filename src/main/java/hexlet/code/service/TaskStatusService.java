package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.exception.TaskStatusAlreadyExistsException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.SlugUtils;
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

    @Transactional(readOnly = true)
    public TaskStatusDTO getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status"
                        + " not found with id: " + id));
        return taskStatusMapper.map(taskStatus);
    }

    @Transactional(readOnly = true)
    public TaskStatus getTaskStatusEntityBySlug(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task status not found with slug: " + slug
                ));
    }


    @Transactional(readOnly = true)
    public List<TaskStatusDTO> getAllTaskStatuses() {
        log.info("GET /api/task_statuses request received");
        return taskStatusRepository.findAll()
                .stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO createTaskStatus(@Valid TaskStatusCreateDTO taskStatusCreateDTO) {
        if (taskStatusRepository.existsByName(taskStatusCreateDTO.getName())) {
            throw new TaskStatusAlreadyExistsException("Task status already "
                    + "exists: " + taskStatusCreateDTO.getName());
        }
        var taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        var taskStatusSaved = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatusSaved);
    }

    public TaskStatusDTO updateTaskStatus(Long id, TaskStatusUpdateDTO dto) {
        TaskStatus existing = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with id: " + id));

        if (dto.getName() != null && !dto.getName().equals(existing.getName())
                && taskStatusRepository.existsByName(dto.getName())) {
            throw new TaskStatusAlreadyExistsException("Task status already exists: " + dto.getName());
        }

        if (dto.getSlug() != null) {
            if (!dto.getSlug().equals(existing.getSlug()) && taskStatusRepository.existsBySlug(dto.getSlug())) {
                throw new TaskStatusAlreadyExistsException("Task status slug already exists: "
                        + dto.getSlug());
            }
        }

        taskStatusMapper.update(dto, existing);

        TaskStatus updated = taskStatusRepository.save(existing);
        return taskStatusMapper.map(updated);
    }


    public void deleteTaskStatus(Long id) {
        taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with id: "
                        + id));
        taskStatusRepository.deleteById(id);
    }

    public Long getTotalCountTaskStatus() {
        return taskStatusRepository.count();
    }


}
