package teccr.justdoitcloud.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import teccr.justdoitcloud.data.Task;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTaskRepository implements TaskRepository {

    private static final String SELECT_COLUMNS = "id, description, created_at, deadline, status, user_id";
    private static final String INSERT_TASK = """
            INSERT INTO tasks (description, created_at, deadline, status, user_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_TASK = """
            UPDATE tasks
            SET description = ?, created_at = ?, deadline = ?, status = ?, user_id = ?
            WHERE id = ?
            """;
    private static final RowMapper<Task> TASK_ROW_MAPPER = JdbcTaskRepository::mapTask;

    private final JdbcTemplate jdbcTemplate;

    public JdbcTaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Task> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM tasks", TASK_ROW_MAPPER);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM tasks WHERE id = ?",
                        TASK_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    @Override
    public List<Task> findByUserId(Long userId) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM tasks WHERE user_id = ?",
                TASK_ROW_MAPPER, userId);
    }

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            insertTask(task);
        } else {
            jdbcTemplate.update(UPDATE_TASK,
                    task.getDescription(),
                    task.getCreatedAt(),
                    task.getDeadline(),
                    task.getStatus() == null ? null : task.getStatus().name(),
                    task.getUserId(),
                    task.getId());
        }
        return task;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM tasks WHERE id = ?", id);
    }

    private void insertTask(Task task) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_TASK, Statement.RETURN_GENERATED_KEYS);
            bindTask(statement, task);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Database did not return an ID for the inserted task");
        }
        task.setId(key.longValue());
    }

    private static void bindTask(PreparedStatement statement, Task task) throws SQLException {
        int index = 1;
        statement.setString(index++, task.getDescription());
        if (task.getCreatedAt() == null) {
            statement.setTimestamp(index++, null);
        } else {
            statement.setTimestamp(index++, Timestamp.valueOf(task.getCreatedAt()));
        }
        if (task.getDeadline() == null) {
            statement.setDate(index++, null);
        } else {
            statement.setDate(index++, Date.valueOf(task.getDeadline()));
        }
        statement.setString(index++, task.getStatus() == null ? null : task.getStatus().name());
        if (task.getUserId() == null) {
            statement.setObject(index, null);
        } else {
            statement.setLong(index, task.getUserId());
        }
    }

    private static Task mapTask(ResultSet resultSet, int rowNumber) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getLong("id"));
        task.setDescription(resultSet.getString("description"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        task.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        Date deadline = resultSet.getDate("deadline");
        task.setDeadline(deadline == null ? null : deadline.toLocalDate());
        String status = resultSet.getString("status");
        task.setStatus(status == null ? null : Task.Status.valueOf(status));
        long userId = resultSet.getLong("user_id");
        task.setUserId(resultSet.wasNull() ? null : userId);
        return task;
    }
}
