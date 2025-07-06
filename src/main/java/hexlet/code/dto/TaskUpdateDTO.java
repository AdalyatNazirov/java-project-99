package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {
    @JsonProperty("title")
    private JsonNullable<String> name;
    private JsonNullable<Integer> index;
    @JsonProperty("content")
    private JsonNullable<String> description;
    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;
    private JsonNullable<String> status;
}
