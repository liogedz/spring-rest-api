package ee.lio.service.impl;

import ee.lio.converter.UserResponseConverter;
import ee.lio.dto.request.PatchRequest;
import ee.lio.dto.request.SavePassword;
import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.request.UpdateRequest;
import ee.lio.dto.response.UserResponse;
import ee.lio.exceptions.ExistingUsernameException;
import ee.lio.exceptions.ForbiddenException;
import ee.lio.exceptions.InvalidIdentifierException;
import ee.lio.exceptions.ResourceNotFoundException;
import ee.lio.model.AuthProvider;
import ee.lio.model.Role;
import ee.lio.model.User;
import ee.lio.repository.UserRepository;
import ee.lio.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserResponseConverter userResponseConverter;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           UserResponseConverter userResponseConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userResponseConverter = userResponseConverter;
    }

    @Transactional
    @Override
    public UserResponse createUser(SignupRequest request) {

        userRepository.findByName(request.getName())
                .orElseThrow(() -> new ExistingUsernameException("Username already taken."));

        userRepository.findByName(request.getEmail())
                .orElseThrow(() -> new ExistingUsernameException("Email already taken."));

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(userResponseConverter.userRequestToUser(request));
        return userResponseConverter.userToUserResponse(savedUser);
    }

    @Transactional
    @Override
    public void savePassword(SavePassword request) {
        User user = userRepository.findById(getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isConfirmed()) {
            throw new ForbiddenException("Password already set");
        }

        AuthProvider provider = user.getProvider();
        if (provider != AuthProvider.GOOGLE && provider != AuthProvider.GITHUB) {
            throw new ForbiddenException("Password setup not allowed");
        }
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setConfirmed(true);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userResponseConverter::userToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(@PathVariable Integer id) {
        User userFound = userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return userResponseConverter.userToUserResponse(userFound);
    }

    @Override
    public User getUserByIdentifier(String identifier) {
        return userRepository.findByNameOrEmail(identifier,
                        identifier)
                .orElseThrow(() ->
                        new InvalidIdentifierException(
                                "User not found with entered credential: " + identifier
                        ));
    }

    @Override
    public UserResponse getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new UsernameNotFoundException("No authenticated user found.");
        }
        String currentUserName = authentication.getName();
        return userRepository.findByName(currentUserName)
                .map(userResponseConverter::userToUserResponse)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentUserName));
    }

    @Transactional
    @Override
    public UserResponse updateUser(UpdateRequest request,
                                   Integer id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        boolean isAdmin = authorizeUserModification(id);

        validateNameUniqueness(request.name(),
                id);
        validateEmailUniqueness(request.email(),
                id);

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        applyRoleChangeIfAllowed(request.role(),
                user,
                isAdmin);
        return userResponseConverter.userToUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponse patchUser(PatchRequest request,
                                  Integer id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        boolean isAdmin = authorizeUserModification(id);
        String name = request.name();
        String email = request.email();

        if (hasValue(request.name())) {
            validateNameUniqueness(name,
                    id);
            user.setName(request.name());
        }
        if (hasValue(request.email())) {
            validateEmailUniqueness(email,
                    id);
            user.setEmail(request.email());
        }
        if (hasValue(request.password())) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        applyRoleChangeIfAllowed(request.role(),
                user,
                isAdmin);
        return userResponseConverter.userToUserResponse(user);
    }

    @Transactional
    @Override
    public User findOrCreateOAuthUser(
            OAuth2User oauthUser,
            String registrationId
    ) {
        AuthProvider provider =
                AuthProvider.valueOf(registrationId.toUpperCase());

        String email = extractEmail(oauthUser,
                provider);
        String providerId = extractProviderId(oauthUser,
                provider);
        String name = extractName(oauthUser,
                provider);

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setName(name);
                    u.setRole(Role.USER);
                    u.setProvider(provider);
                    u.setProviderId(providerId);
                    u.setEnabled(true);
                    return userRepository.save(u);
                });
    }


    @Transactional
    @Override
    public void deleteUser(Integer id) {
        userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        UserResponse currentUser = getCurrentUser();
        if (!currentUser.getId().equals(id) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Do not have permission to delete this user.");
        }
        userRepository.deleteById(id);
    }

    private boolean authorizeUserModification(Integer targetUserId) {
        UserResponse currentUser = getCurrentUser();

        boolean isSelf = currentUser.getId().equals(targetUserId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isSelf && !isAdmin) {
            throw new ForbiddenException("You don't have permission to modify this user.");
        }

        return isAdmin;
    }

    private void applyRoleChangeIfAllowed(Role newRole,
                                          User user,
                                          boolean isAdmin) {
        if (newRole != null && !newRole.equals(user.getRole())) {
            if (!isAdmin) {
                throw new ForbiddenException("Only administrators can change user roles.");
            }
            user.setRole(newRole);
        }
    }

    private void validateNameUniqueness(String name,
                                        Integer userId) {
        userRepository.findByNameAndIdNot(name,
                        userId)
                .ifPresent(u -> {
                    throw new ExistingUsernameException("Username already taken.");
                });
    }

    private void validateEmailUniqueness(String email,
                                         Integer userId) {
        userRepository.findByEmailAndIdNot(email,
                        userId)
                .ifPresent(u -> {
                    throw new ExistingUsernameException("Email already taken.");
                });
    }

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }

    private String extractEmail(OAuth2User user,
                                AuthProvider provider) {
        String email = user.getAttribute("email");

        if (email == null) {
            throw new OAuth2AuthenticationException(
                    "Email not provided by " + provider
            );
        }

        return email;
    }

    private String extractProviderId(OAuth2User user,
                                     AuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> user.getAttribute("sub");
            case GITHUB -> Objects.requireNonNull(user.getAttribute("id")).toString();
            case LOCAL -> null;
        };
    }

    private String extractName(OAuth2User user,
                               AuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> user.getAttribute("name");
            case GITHUB -> {
                String name = user.getAttribute("name");
                yield name != null ? name : user.getAttribute("login");
            }
            case LOCAL -> null;
        };
    }


}
