package ee.lio.service.impl;

import ee.lio.converter.UserResponseConverter;
import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.response.UserResponse;
import ee.lio.exceptions.ExistingUsernameException;
import ee.lio.exceptions.ResourceNotFoundException;
import ee.lio.model.User;
import ee.lio.repository.UserRepository;
import ee.lio.service.UserService;
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

        Optional<User> optionalUser = userRepository.findUserByName(request.getName());
        if (optionalUser.isPresent()) {
            throw new ExistingUsernameException("Username already taken.");
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
        Optional<User> userFound = userRepository.findById(id);
        if (userFound.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return userResponseConverter.userToUserResponse(userFound.get());
    }
}
