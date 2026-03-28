package ee.lio.service;

import ee.lio.dto.request.PatchRequest;
import ee.lio.dto.request.SavePassword;
import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.request.UpdateRequest;
import ee.lio.dto.response.UserResponse;
import ee.lio.model.User;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserService {


    UserResponse createUser(SignupRequest request);

    void savePassword(SavePassword request);

    Page<UserResponse> getAllUsers(int page,
                                   int size,
                                   String search,
                                   String sortBy,
                                   String sortDir);


    UserResponse getUserById(Integer id);

    User getUserByIdentifier(String identifier);

    UserResponse getCurrentUser();

    void deleteUser(Integer id);

    UserResponse updateUser(UpdateRequest request,
                            Integer id);

    UserResponse patchUser(PatchRequest request,
                           Integer id);

    User findOrCreateOAuthUser(OAuth2User oauthUser,
                               String registrationId);
}
