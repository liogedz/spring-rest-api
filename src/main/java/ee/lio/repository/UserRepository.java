package ee.lio.repository;

import ee.lio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByName(String name);

    Optional<User> findUserById(Integer id);

    Optional<User> findByNameOrEmail(String name,
                                     String email);

    void deleteById(Integer id);

    Optional<User> findByNameAndIdNot(String name,
                                      Integer id);

    Optional<User> findByEmailAndIdNot(String email,
                                       Integer id);

}