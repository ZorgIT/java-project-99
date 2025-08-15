package hexlet.code.app.controller;


import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    private final TaskStatusService taskStatusService;

    @Autowired
    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public ResponseEntity<List<TaskStatusDTO>> getAllTaskStatuses() {
        List<TaskStatusDTO> taskStatuses = taskStatusService.getAllTaskStatus();
        long totalCount = taskStatuses.size();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalCount))
                .body(taskStatuses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> getTaskStatusById(@PathVariable("id") long id) {
        TaskStatusDTO taskStatusDTO = taskStatusService.getTaskStatusById(id);
        return taskStatusDTO != null ? ResponseEntity.ok(taskStatusDTO) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TaskStatusDTO> createTaskStatus(@RequestBody @Valid TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatusDTO createdTaskStatus =
                taskStatusService.createTaskStatus(taskStatusCreateDTO);
        return createdTaskStatus != null ? ResponseEntity.ok(createdTaskStatus) : ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<TaskStatusDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody @Valid TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatusDTO updatedTaskStatus =
                taskStatusService.updateTaskStatus(id, taskStatusUpdateDTO);
        return ResponseEntity.ok(updatedTaskStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Long id) {
        //TODO добавить валидацию прав на удаление либо отрегулировать
        // политику в секьютири конфиге
        taskStatusService.deleteTaskStatus(id);
        return ResponseEntity.noContent().build();
    }


}
