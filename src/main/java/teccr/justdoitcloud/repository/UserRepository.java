package teccr.justdoitcloud.repository;

import teccr.justdoitcloud.data.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByUserName(String userName);
    User save(User user);
    void deleteById(Long id);
}
