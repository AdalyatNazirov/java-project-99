package hexlet.code.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
@AllArgsConstructor
@Valid
public class LabelUpdateDTO {
    @NotBlank
    private JsonNullable<String> name;
}
