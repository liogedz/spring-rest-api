package ee.lio.service.impl;

import ee.lio.model.User;
import ee.lio.security.UserDetailsAdapter;
import ee.lio.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String identifier) throws UsernameNotFoundException {
        User user = userService.getUserByIdentifier(identifier);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with entered credential: " + identifier);
        }
        
        return new UserDetailsAdapter(user);
    }
}
