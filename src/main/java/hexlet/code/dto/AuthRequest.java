package hexlet.code.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Valid
public class AuthRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
