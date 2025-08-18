package hexlet.code.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
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

    @NotBlank
    @Size(min = 1)
    private String name;

    @Column(nullable = true)
    private Integer index;

    @Column(nullable = true)
    private String description;

    // ! Много задач могут иметь один статус
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private TaskStatus status;


    // ! Много задач может быть назначено одному пользователю
    @ManyToOne
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate createdAt;

}
