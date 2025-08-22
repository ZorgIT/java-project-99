package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.TaskService;
import hexlet.code.app.specification.TaskSpecBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    private final TaskSpecBuilder taskSpecBuilder;

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;


    @Autowired
    public TaskController(TaskService taskService,
                          TaskSpecBuilder taskSpecBuilder,
                          TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskSpecBuilder = taskSpecBuilder;
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }


    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @RequestParam(required = false) String titleCont,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long labelId,
            @RequestParam(defaultValue = "1") int page
    ) {

        TaskParamsDTO params = new TaskParamsDTO(titleCont, assigneeId, status, labelId);

        Specification<Task> spec = taskSpecBuilder.build(params);

        Page<Task> tasksPage = taskRepository.findAll(spec, PageRequest.of(page - 1, 10));

        List<TaskDTO> tasks = tasksPage.getContent().stream()
                .map(taskMapper::map)
                .collect(Collectors.toList());

        long totalCount = tasksPage.getTotalElements();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalCount))
                .body(tasks);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable("id") long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody @Valid TaskCreateDTO dto) {
        TaskDTO createdTask = taskService.createTask(dto);
        return ResponseEntity.ok(createdTask);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT,
            RequestMethod.PATCH})
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id,
                                              @RequestBody @Valid TaskUpdateDTO dto) {
        TaskDTO updatedTask = taskService.updateTask(id, dto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }


}
