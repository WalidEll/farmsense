package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.user.UpdateLocationRequest;
import ma.farmsense.entity.User;
import ma.farmsense.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PutMapping("/location")
    public ResponseEntity<Void> updateLocation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateLocationRequest req) {
        user.setLatitude(req.getLatitude());
        user.setLongitude(req.getLongitude());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}
