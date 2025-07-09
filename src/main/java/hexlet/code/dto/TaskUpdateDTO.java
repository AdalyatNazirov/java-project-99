package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Data
@Valid
public class TaskUpdateDTO {
    @JsonProperty("title")
    @JsonAlias({"name"})
    private JsonNullable<String> name;
    private JsonNullable<Integer> index;
    @JsonProperty("content")
    private JsonNullable<String> description;
    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;
    private JsonNullable<String> status;
    @JsonProperty("taskLabelIds")
    private JsonNullable<Set<Long>> labelIds;
}
