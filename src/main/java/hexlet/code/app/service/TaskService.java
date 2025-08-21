package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.specification.TaskSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       TaskMapper taskMapper,
                       UserRepository userRepository,
                       TaskStatusRepository taskStatusRepository,
                       LabelRepository labelRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.labelRepository = labelRepository;
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.map(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO createTask(TaskCreateDTO dto) {
        Task task = taskMapper.map(dto);
        applyStatusAndAssignee(task, dto.getStatus(), dto.getAssigneeId(),
                dto.getLabelsId());
        taskRepository.save(task);
        TaskDTO updated = taskMapper.map(task);
        return updated;
    }

    public TaskDTO updateTask(Long id, TaskUpdateDTO dto) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        taskMapper.update(dto, existing);
        applyStatusAndAssignee(existing, dto.getStatus(), dto.getAssigneeId()
                , dto.getLabelsId());

        var updated = taskRepository.save(existing);
        return taskMapper.map(updated);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    private TaskStatus getTaskStatus(String name) {
        return taskStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with name: " + name));
    }

    private TaskStatus getTaskStatusBySlug(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with slug: " + slug));
    }

    private User getAssignee(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Set<Label> getLabels(Set<Long> ids) {
        return new HashSet<>(labelRepository.findAllById(ids));
    }

    private void applyStatusAndAssignee(Task task, String statusSlug,
                                        Long assigneeId, Set<Long> labelsId) {
        if (statusSlug != null) {
            task.setStatus(getTaskStatusBySlug(statusSlug));
        }
        if (assigneeId != null) {
            task.setAssignee(getAssignee(assigneeId));
        }

        if (labelsId != null && !labelsId.isEmpty()) {
            task.setLabels(getLabels(labelsId));
        }
    }

    public List<TaskDTO> getTasks(String titleCont, Long assigneeId,
                                  String status, Long labelId) {
        //если нужен или - anyof
        Specification<Task> spec = Specification.allOf(
                TaskSpecifications.titleContains(titleCont),
                TaskSpecifications.hasAssignee(assigneeId),
                TaskSpecifications.hasStatus(status),
                TaskSpecifications.hasLabel(labelId)
        );

        return taskRepository.findAll(spec)
                .stream()
                .map(taskMapper::map)
                .collect(Collectors.toList());
    }
}
