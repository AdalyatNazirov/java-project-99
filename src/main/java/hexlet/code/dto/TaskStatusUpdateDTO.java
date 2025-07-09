package hexlet.code.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
@Valid
public class TaskStatusUpdateDTO {
    @NotBlank
    private JsonNullable<String> name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$")
    private JsonNullable<String> slug;
}
