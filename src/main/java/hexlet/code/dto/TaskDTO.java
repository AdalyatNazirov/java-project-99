package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
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
    private LocalDate createdAt;
    @JsonProperty("taskLabelIds")
    private Set<Long> labelIds;
}
