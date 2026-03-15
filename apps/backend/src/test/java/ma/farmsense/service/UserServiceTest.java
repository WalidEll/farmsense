package ma.farmsense.service;

import ma.farmsense.dto.user.UpdateLocationRequest;
import ma.farmsense.entity.User;
import ma.farmsense.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void updateLocation_ShouldSetCoordinates_AndSave() {
        User user = User.builder().id(UUID.randomUUID()).build();
        UpdateLocationRequest req = new UpdateLocationRequest(33.5731, -7.5898);

        userService.updateLocation(user, req);

        assertEquals(33.5731, user.getLatitude());
        assertEquals(-7.5898, user.getLongitude());
        verify(userRepository).save(user);
    }
}
