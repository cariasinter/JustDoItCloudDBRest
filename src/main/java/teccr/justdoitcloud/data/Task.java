package teccr.justdoitcloud.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class Task {
    private Long id;
    @Size(min=3, message = "Descripcion debe tener al menos 3 caracteres")
    private String description;
    private LocalDateTime createdAt;
    private LocalDate deadline;
    @NotNull
    private Status status;

    private Long userId;

    public enum Status {
        PENDING,
        INPROGRESS,
        DONE
    }
}
