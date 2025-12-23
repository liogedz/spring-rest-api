package ee.lio.controller;

import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.response.ApiResponse;
import ee.lio.dto.response.UserResponse;
import ee.lio.exceptions.DataNotValidatedException;
import ee.lio.service.UserService;
import ee.lio.service.impl.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserServiceImpl userService
    ) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse("User list",
                users));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addNewUser(@Validated @RequestBody
                                                  SignupRequest request,
                                                  Errors errors) {
        if (errors.hasErrors()) {
            String errorDetails = errors.getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new DataNotValidatedException(errorDetails);
        }
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("User creation success",
                response));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Integer id) {

        return ResponseEntity.ok(new ApiResponse("User found",
                userService.getUserById(id)
        ));
    }
}
