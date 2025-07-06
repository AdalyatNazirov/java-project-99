package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
@AllArgsConstructor
public class LabelUpdateDTO {
    private JsonNullable<String> name;
}
