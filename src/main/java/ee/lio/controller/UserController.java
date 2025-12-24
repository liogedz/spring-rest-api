package ee.lio.controller;

import ee.lio.dto.response.ApiResponse;
import ee.lio.dto.response.UserResponse;
import ee.lio.service.UserService;
import ee.lio.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Integer id) {

        return ResponseEntity.ok(new ApiResponse("User found",
                userService.getUserById(id)
        ));
    }
}
