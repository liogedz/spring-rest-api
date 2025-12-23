package ee.lio.service.impl;

import ee.lio.model.User;
import ee.lio.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsImpl implements UserDetailsService {
    private final UserService userService;

    public UserDetailsImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User loadUserByUsername(@NonNull String identifier) {
        User returnValue = null;
        Optional<User> userOpt = Optional.ofNullable(userService.getUserByIdentifier(identifier));
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with entered credential: " + identifier);
        } else {
            returnValue = userOpt.get();
        }

        return returnValue;
    }
}
