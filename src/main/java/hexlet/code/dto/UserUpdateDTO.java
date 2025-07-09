package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
@Valid
public class UserUpdateDTO {
    @NotBlank
    private JsonNullable<String> firstName;
    @NotBlank
    private JsonNullable<String> lastName;
    @Email
    private JsonNullable<String> email;
    @NotNull
    @Size(min = 3, max = 100)
    @JsonProperty("password")
    private JsonNullable<String> passwordDigest;
}
