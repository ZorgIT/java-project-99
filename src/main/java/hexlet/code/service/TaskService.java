package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecifications;
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
    private final TaskStatusService taskStatusService;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       TaskMapper taskMapper,
                       UserRepository userRepository,
                       TaskStatusRepository taskStatusRepository,
                       LabelRepository labelRepository,
                       TaskStatusService taskStatusService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.labelRepository = labelRepository;
        this.taskStatusService = taskStatusService;
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: "
                        + id));
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
        applyStatusAndAssignee(existing, dto.getStatus(), dto.getAssigneeId(), dto.getLabelsId());

        var updated = taskRepository.save(existing);
        return taskMapper.map(updated);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    private TaskStatus getTaskStatusByName(String name) {
        return taskStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with name: " + name));
    }

    private TaskStatus getTaskStatusBySlug(String slug) {
        return taskStatusService.getTaskStatusEntityBySlug(slug);
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
