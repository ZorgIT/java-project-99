package hexlet.code.specification;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Component
@RequiredArgsConstructor
public class TaskSpecBuilder {

    private final TaskRepository taskRepository;

    public Specification<Task> build(TaskParamsDTO params) {
        return Specification.where(withTitleCont(params.getTitleCont()))
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) ->
                titleCont != null ? cb.like(root.get("name"), "%" + titleCont + "%") : null;
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId != null ? cb.equal(root.get("assignee").get("id"), assigneeId) : null;
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            // Присоединяем отношение к статусу и сравниваем по slug
            Join<Task, TaskStatus> statusJoin = root.join("status", JoinType.INNER);
            return cb.equal(statusJoin.get("slug"), status);
        };
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return null;
            }
            // Присоединяем отношение к labels и проверяем ID
            Join<Task, Label> labelsJoin = root.join("labels", JoinType.INNER);
            return cb.equal(labelsJoin.get("id"), labelId);
        };
    }
}
