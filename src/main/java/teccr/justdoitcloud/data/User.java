package teccr.justdoitcloud.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class User {
    private Long id;
    private String userName;
    private String name;
    private String email;
    private Type type;
    @JsonIgnore // No incluir esta propiedad en las respuestas JSON de la API
    private List<Task> tasks;
    private LocalDateTime createdAt;

    public enum Type {
        ADMIN,
        REGULAR
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
