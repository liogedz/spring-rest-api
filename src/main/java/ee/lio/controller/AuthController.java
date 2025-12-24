package ee.lio.controller;

import ee.lio.converter.UserResponseConverter;
import ee.lio.dto.request.LoginRequest;
import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.response.ApiResponse;
import ee.lio.dto.response.UserResponse;
import ee.lio.exceptions.DataNotValidatedException;
import ee.lio.model.User;
import ee.lio.service.UserService;
import ee.lio.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final UserResponseConverter userResponseConverter;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtTokenUtil,
                          UserResponseConverter userResponseConverter) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userResponseConverter = userResponseConverter;
    }

    @PostMapping(value = "signup")
    public ResponseEntity<ApiResponse> register(@Validated @RequestBody SignupRequest request,
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

    @PostMapping("login")
    public ResponseEntity<ApiResponse> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getIdentifier(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Invalid username or password",
                            null));
        }

        User user = userService.getUserByIdentifier(loginRequest.getIdentifier());
        String jwt = jwtTokenUtil.generateToken(user);
        UserResponse userResponse = userResponseConverter.userToUserResponse(user);
        userResponse.setAuthToken(jwt);

        return ResponseEntity.ok(new ApiResponse("Login successful",
                userResponse));
    }
}
