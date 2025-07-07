package hexlet.code.specification;

import hexlet.code.dto.TaskListDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskListDTO params) {
        return withAssigneeId(params.getAssigneeId())
                .and(withTitleContains(params.getTitle()))
                .and(withStatusEq(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null
                ? cb.conjunction()
                : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withTitleContains(String titleCont) {
        return (root, query, cb) -> titleCont == null
                ? cb.conjunction()
                : cb.greaterThan(root.get("name"), titleCont);
    }

    private Specification<Task> withStatusEq(String statusEq) {
        return (root, query, cb) -> statusEq == null
                ? cb.conjunction()
                : cb.equal(root.get("taskStatus").get("slug"), statusEq);
    }

    private Specification<Task> withLabelId(Long labelIdIn) {
        return (root, query, cb) -> {
            if (labelIdIn == null) {
                return cb.conjunction();
            }

            Join<Task, Label> labelJoin = root.join("labels");
            return cb.equal(labelJoin.get("id"), labelIdIn);
        };
    }
}
