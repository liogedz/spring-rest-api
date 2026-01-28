package ee.lio.service;

import ee.lio.dto.request.PatchRequest;
import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.request.UpdateRequest;
import ee.lio.dto.response.UserResponse;
import ee.lio.model.User;

import java.util.List;


public interface UserService {


    UserResponse createUser(SignupRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Integer id);

    User getUserByIdentifier(String identifier);

    UserResponse getCurrentUser();

    void deleteUser(Integer id);

    UserResponse updateUser(UpdateRequest request,
                            Integer id);


    UserResponse patchUser(PatchRequest request,
                           Integer id);
}
