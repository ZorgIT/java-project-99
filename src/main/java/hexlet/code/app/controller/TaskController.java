package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTask() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        long totalCount = tasks.size();
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
        //TODO добавить валидацию прав на удаление либо отрегулировать
        // политику в секьюрити конфиге
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
