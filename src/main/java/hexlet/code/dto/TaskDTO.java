package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    @JsonProperty("title")
    private String name;
    private Integer index;
    @JsonProperty("content")
    private String description;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private String status;
    private Instant createdAt;
}
