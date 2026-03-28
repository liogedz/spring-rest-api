package ee.lio.repository;

import ee.lio.model.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByName(String name);

    Optional<User> findUserById(Integer id);

    Optional<User> findByNameOrEmail(String name,
                                     String email);

    Optional<User> findByNameAndIdNot(String name,
                                      Integer id);

    Optional<User> findByEmailAndIdNot(String email,
                                       Integer id);

    Optional<User> findByEmail(String email);

    void deleteById(@NonNull Integer id);

    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.name)  LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    Page<User> search(@Param("q") String query,
                      Pageable pageable);
}