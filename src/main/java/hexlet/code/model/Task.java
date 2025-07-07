package hexlet.code.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import hexlet.code.util.DateStringToInstantDeserializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1)
    private String name;

    private Integer index;

    private String description;

    @ManyToOne
    private TaskStatus taskStatus;

    @ManyToOne
    private User assignee;

    @CreatedDate
    @JsonDeserialize(using = DateStringToInstantDeserializer.class)
    private Instant createdAt;

    @ManyToMany
    private List<Label> labels = new ArrayList<>();
}
