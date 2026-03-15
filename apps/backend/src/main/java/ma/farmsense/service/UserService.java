package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.user.UpdateLocationRequest;
import ma.farmsense.entity.User;
import ma.farmsense.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateLocation(User user, UpdateLocationRequest req) {
        user.setLatitude(req.latitude());
        user.setLongitude(req.longitude());
        userRepository.save(user);
    }
}
