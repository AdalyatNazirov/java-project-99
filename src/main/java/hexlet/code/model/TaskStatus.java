package hexlet.code.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import hexlet.code.util.DateStringToInstantDeserializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "task_statuses")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatus implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    @NotBlank
    private String name;

    @Column(unique = true)
    @NotBlank
    private String slug;

    @CreatedDate
    @JsonDeserialize(using = DateStringToInstantDeserializer.class)
    private Instant createdAt;

    public TaskStatus(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
