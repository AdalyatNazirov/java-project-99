package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskListDTO {
    private String title;
    private Long assigneeId;
    private String status;
    private Long labelId;
}
