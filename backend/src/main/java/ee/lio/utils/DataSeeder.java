package ee.lio.utils;

import com.github.javafaker.Faker;
import ee.lio.model.AuthProvider;
import ee.lio.model.Role;
import ee.lio.model.User;
import ee.lio.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@PreAuthorize("hasRole('ADMIN')")
public class DataSeeder {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(Locale.ENGLISH);

    public DataSeeder(UserRepository userRepository,
                      BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void seedUsers() {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            User user = new User();
            user.setName(faker.name().fullName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode(faker.internet().password(8,
                    16)));
            user.setRole(Role.ADMIN);
            user.setProvider(AuthProvider.LOCAL);
            user.setProviderId(null);
            user.setEnabled(true);
            user.setConfirmed(true);
            users.add(user);
        }

        for (int i = 0; i < 11; i++) {
            User user = new User();
            user.setName(faker.name().fullName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode(faker.internet().password(8,
                    16)));
            user.setRole(Role.USER);
            user.setProvider(AuthProvider.LOCAL);
            user.setProviderId(null);
            user.setEnabled(true);
            user.setConfirmed(true);
            users.add(user);
        }

        for (int i = 0; i < 11; i++) {
            User user = new User();
            user.setName(faker.name().fullName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode(faker.internet().password(8,
                    16)));
            user.setRole(Role.USER);
            user.setProvider(AuthProvider.GOOGLE);
            user.setProviderId(faker.idNumber().valid());
            user.setEnabled(true);
            user.setConfirmed(true);
            users.add(user);
        }
        
        for (int i = 0; i < 11; i++) {
            User user = new User();
            user.setName(faker.name().fullName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode(faker.internet().password(8,
                    16)));
            user.setRole(Role.USER);
            user.setProvider(AuthProvider.GITHUB);
            user.setProviderId(String.valueOf(faker.number().numberBetween(1000000,
                    9999999)));
            user.setEnabled(true);
            user.setConfirmed(true);
            users.add(user);
        }

        userRepository.saveAll(users);
    }
}