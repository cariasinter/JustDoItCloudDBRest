package teccr.justdoitcloud.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import teccr.justdoitcloud.data.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    private static final String SELECT_COLUMNS = "id, user_name, name, email, type, created_at";
    private static final String INSERT_USER = """
            INSERT INTO users (user_name, name, email, type, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_USER = """
            UPDATE users
            SET user_name = ?, name = ?, email = ?, type = ?, created_at = ?
            WHERE id = ?
            """;
    private static final RowMapper<User> USER_ROW_MAPPER = JdbcUserRepository::mapUser;

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM users", USER_ROW_MAPPER);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM users WHERE id = ?",
                        USER_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM users WHERE user_name = ?",
                        USER_ROW_MAPPER, userName)
                .stream()
                .findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            insertUser(user);
        } else {
            jdbcTemplate.update(UPDATE_USER,
                    user.getUserName(),
                    user.getName(),
                    user.getEmail(),
                    user.getType() == null ? null : user.getType().name(),
                    user.getCreatedAt(),
                    user.getId());
        }
        return user;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    private void insertUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            bindUser(statement, user);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Database did not return an ID for the inserted user");
        }
        user.setId(key.longValue());
    }

    private static void bindUser(PreparedStatement statement, User user) throws SQLException {
        int index = 1;
        statement.setString(index++, user.getUserName());
        statement.setString(index++, user.getName());
        statement.setString(index++, user.getEmail());
        statement.setString(index++, user.getType() == null ? null : user.getType().name());
        if (user.getCreatedAt() == null) {
            statement.setTimestamp(index, null);
        } else {
            statement.setTimestamp(index, Timestamp.valueOf(user.getCreatedAt()));
        }
    }

    private static User mapUser(ResultSet resultSet, int rowNumber) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUserName(resultSet.getString("user_name"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        String type = resultSet.getString("type");
        user.setType(type == null ? null : User.Type.valueOf(type));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        user.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        return user;
    }
}
