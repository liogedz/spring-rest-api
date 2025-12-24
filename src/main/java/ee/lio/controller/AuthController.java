package ee.lio.controller;

import ee.lio.converter.UserResponseConverter;
import ee.lio.dto.request.LoginRequest;
import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.request.TwoFactorRequest;
import ee.lio.dto.response.ApiResponse;
import ee.lio.dto.response.UserResponse;
import ee.lio.exceptions.DataNotValidatedException;
import ee.lio.model.User;
import ee.lio.service.EmailService;
import ee.lio.service.TwoFactorService;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final UserResponseConverter userResponseConverter;
    private final TwoFactorService twoFactorService;
    private final EmailService emailService;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtTokenUtil,
                          UserResponseConverter userResponseConverter,
                          TwoFactorService twoFactorService,
                          EmailService emailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userResponseConverter = userResponseConverter;
        this.twoFactorService = twoFactorService;
        this.emailService = emailService;
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
        String identifier = loginRequest.getIdentifier();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            identifier,
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Invalid username or password",
                            null));
        }
        User user = userService.getUserByIdentifier(identifier);
        String code = twoFactorService.generateAndStoreCode(identifier);
        emailService.send2FACode(user.getEmail(),
                code);

        return ResponseEntity.ok(new ApiResponse("Login successful. A verification code has been sent to your email.",
                null));
    }

    @PostMapping(value = "verify")
    public ResponseEntity<ApiResponse> verifyUser(@Validated @RequestBody TwoFactorRequest request) {

        String identifier = request.identifier();
        String submittedCode = request.code();

        if (!twoFactorService.validateCode(identifier,
                submittedCode)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid verification code");
        }
        twoFactorService.clearCode(identifier);

        User user = userService.getUserByIdentifier(identifier);
        String jwt = jwtTokenUtil.generateToken(user);

        UserResponse userResponse = userResponseConverter.userToUserResponse(user);
        userResponse.setAuthToken(jwt);

        return ResponseEntity.ok(new ApiResponse("Login successful",
                userResponse));
    }
}
