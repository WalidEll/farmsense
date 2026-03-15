package ma.farmsense.service;

import ma.farmsense.dto.user.UpdateLocationRequest;
import ma.farmsense.entity.User;
import ma.farmsense.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void updateLocation(User user, UpdateLocationRequest req) {
        user.setLatitude(req.latitude());
        user.setLongitude(req.longitude());
        userRepository.save(user);
    }
}
