package hexlet.code.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "assignees")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer index;

    private String description;

    // Много задач могут иметь один статус
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private TaskStatus status;


    // Много задач может быть назначено одному пользователю
    @ManyToOne
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

    private LocalDate createdAt;

}
