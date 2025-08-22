package hexlet.code.app.model;

import hexlet.code.app.utils.SlugUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_statuses", indexes = @Index(name = "idx_task_status_slug", columnList = "slug"))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskStatus implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1)
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Size(min = 1)
    @Column(nullable = false, unique = true)
    private String slug;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Один статус может быть у многих задач
    @OneToMany(mappedBy = "status")
    private List<Task> tasks = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.slug == null && this.name != null) {
            this.slug = SlugUtils.generateSlug(this.name);
        }
    }

    public static TaskStatus of(String name) {
        TaskStatus status = new TaskStatus();
        status.setName(name);
        return status;
    }

}
