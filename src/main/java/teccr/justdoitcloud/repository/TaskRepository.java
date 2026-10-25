package teccr.justdoitcloud.repository;

import teccr.justdoitcloud.data.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAll();
    Optional<Task> findById(Long id);
    List<Task> findByUserId(Long userId);
    Task save(Task task);
    void deleteById(Long id);
}
