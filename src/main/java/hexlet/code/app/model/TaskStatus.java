package hexlet.code.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.commons.text.CaseUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_statuses", indexes = @Index(name = "idx_task_status_slug", columnList = "slug"))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskStatus implements BaseEntity {

    @PrePersist
    public void prePersist() {
        if (this.slug == null && this.name != null) {
            this.slug = generateSlug(this.name);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1)
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Size(min = 1)
    @Column(updatable = false, nullable = false, unique = true)
    private String slug;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static TaskStatus of(String name) {
        TaskStatus status = new TaskStatus();
        status.setName(name);
        return status;
    }

    public void setName(String name) {
        this.name = name;
        this.slug = generateSlug(name);
    }

    private String generateSlug(String name) {
        String slug = CaseUtils.toCamelCase(name, false, '-')
                .toLowerCase()
                .replaceAll("[^a-z0-9-]", "");

        if (slug.isBlank()) {
            throw new IllegalArgumentException("Cannot generate slug from name: " + name);
        }
        return slug;
    }

}
