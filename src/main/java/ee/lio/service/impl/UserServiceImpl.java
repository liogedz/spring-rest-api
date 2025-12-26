package ee.lio.service.impl;

import ee.lio.converter.UserResponseConverter;
import ee.lio.dto.request.PatchRequest;
import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.request.UpdateRequest;
import ee.lio.dto.response.UserResponse;
import ee.lio.exceptions.ExistingUsernameException;
import ee.lio.exceptions.ForbiddenException;
import ee.lio.exceptions.ResourceNotFoundException;
import ee.lio.model.Role;
import ee.lio.model.User;
import ee.lio.repository.UserRepository;
import ee.lio.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
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

    @Override
    public UserResponse createUser(SignupRequest request) {

        Optional<User> userByName = userRepository.findByName(request.getName());
        if (userByName.isPresent()) {
            throw new ExistingUsernameException("Username already taken.");
        }
        Optional<User> userByEmail = userRepository.findByName(request.getEmail());
        if (userByEmail.isPresent()) {
            throw new ExistingUsernameException("Email already taken.");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(userResponseConverter.userRequestToUser(request));
        return userResponseConverter.userToUserResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userResponseConverter::userToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(@PathVariable Integer id) {
        Optional<User> userFound = userRepository.findUserById(id);
        if (userFound.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return userResponseConverter.userToUserResponse(userFound.get());
    }

    @Override
    public User getUserByIdentifier(String identifier) {
        User returnValue;
        Optional<User> userOptional = userRepository.findByNameOrEmail(identifier,
                identifier);
        if (userOptional.isPresent()) {
            returnValue = userOptional.get();
        } else {
            throw new UsernameNotFoundException("User not found with entered credential: " + identifier);
        }
        return returnValue;
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

        userRepository.save(user);
        return userResponseConverter.userToUserResponse(user);
    }


    @Override
    public UserResponse patchUser(PatchRequest request,
                                  Integer id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        boolean isAdmin = authorizeUserModification(id);
        String name = request.name();
        String email = request.email();

        if (name != null) {
            validateNameUniqueness(name,
                    id);
            user.setName(request.name());
        }
        if (email != null) {
            validateEmailUniqueness(email,
                    id);
            user.setEmail(request.email());
        }
        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        applyRoleChangeIfAllowed(request.role(),
                user,
                isAdmin);

        userRepository.save(user);
        return userResponseConverter.userToUserResponse(user);
    }


    @Override
    public void deleteUser(Integer id) {
        Optional<User> optUser = userRepository.findUserById(id);
        if (optUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
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


}
