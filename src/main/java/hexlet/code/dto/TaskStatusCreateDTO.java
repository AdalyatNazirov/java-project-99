package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskStatusCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String slug;
}
