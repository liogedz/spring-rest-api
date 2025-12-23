package ee.lio.service;

import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.response.UserResponse;

import java.util.List;


public interface UserService {


    UserResponse createUser(SignupRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Integer id);
}
