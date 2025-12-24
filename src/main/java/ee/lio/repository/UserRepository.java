package ee.lio.repository;

import ee.lio.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByName(String name);

    List<User> findAll();

    Optional<User> findUserById(Integer id);

    Optional<User> findByNameOrEmail(String name,
                                     String email);
}