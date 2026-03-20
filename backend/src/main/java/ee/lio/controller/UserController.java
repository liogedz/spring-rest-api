package ee.lio.controller;

import ee.lio.dto.request.PatchRequest;
import ee.lio.dto.request.UpdateRequest;
import ee.lio.dto.response.ApiResponse;
import ee.lio.dto.response.UserResponse;
import ee.lio.service.UserService;
import ee.lio.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserServiceImpl userService
    ) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Integer id) {

        return ResponseEntity.ok(new ApiResponse<>("User found",
                userService.getUserById(id)
        ));
    }

    @GetMapping("current")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        return ResponseEntity.ok(new ApiResponse<>("Current User",
                userService.getCurrentUser()));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UpdateRequest request,
                                                                @PathVariable Integer id) {
        UserResponse updatedUser = userService.updateUser(request,
                id);
        return ResponseEntity.ok(new ApiResponse<>("User with id: " + id + " updated successfully",
                updatedUser));
    }

    @PatchMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> patchUser(@RequestBody
                                                               PatchRequest request,
                                                               @PathVariable Integer id) {
        System.out.println(request.toString());
        UserResponse patchedUser = userService.patchUser(request,
                id);
        return ResponseEntity.ok(new ApiResponse<>("User with id: " + id + " patched successfully",
                patchedUser));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> removeUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("User deleted with id: " + id,
                null));
    }
}
