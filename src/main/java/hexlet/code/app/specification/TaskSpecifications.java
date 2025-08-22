package hexlet.code.app.specification;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {
    public static Specification<Task> titleContains(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null ? null : cb.equal(root.get("assigneeId"), assigneeId);
    }

    public static Specification<Task> hasStatus(String status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasLabel(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return null;
            }
            return cb.equal(root.join("labels").get("id"), labelId);
        };
    }
}
