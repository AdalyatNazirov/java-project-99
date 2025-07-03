package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDTO {
    @NotBlank
    private JsonNullable<String> name;

    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private JsonNullable<String> slug;
}
