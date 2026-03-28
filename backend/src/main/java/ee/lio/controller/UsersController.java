package ee.lio.controller;

import ee.lio.dto.response.ApiResponse;
import ee.lio.dto.response.PagedResponse;
import ee.lio.dto.response.UserResponse;
import ee.lio.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        if (size > 100) size = 100;
        Page<UserResponse> pageData =
                userService.getAllUsers(page,
                        size,
                        search,
                        sortBy,
                        sortDir);
        return ResponseEntity.ok(new ApiResponse<>("User list",
                new PagedResponse<>(pageData)));
    }
}
