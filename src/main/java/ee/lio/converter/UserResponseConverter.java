package ee.lio.converter;

import ee.lio.dto.request.SignupRequest;
import ee.lio.dto.response.UserResponse;
import ee.lio.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserResponseConverter {
    private final ModelMapper mapper;

    public UserResponseConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public User userRequestToUser(SignupRequest signupRequest) {
        return mapper.map(signupRequest,
                User.class);
    }

    public UserResponse userToUserResponse(User user) {
        return mapper.map(user,
                UserResponse.class);
    }
}
