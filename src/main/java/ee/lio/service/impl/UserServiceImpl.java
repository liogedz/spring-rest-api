package ee.lio.service.impl;

import ee.lio.converter.UserResponseConverter;
import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.response.UserResponse;
import ee.lio.exceptions.ResourceNotFoundException;
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
            throw new ResourceNotFoundException("Username already taken.");
        }
        Optional<User> userByEmail = userRepository.findByName(request.getName());
        if (userByEmail.isPresent()) {
            throw new ResourceNotFoundException("Email already taken.");
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
        User returnValue = null;
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
}
