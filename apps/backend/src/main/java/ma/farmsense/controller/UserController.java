package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.user.UpdateLocationRequest;
import ma.farmsense.entity.User;
import ma.farmsense.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/location")
    public ResponseEntity<Void> updateLocation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateLocationRequest req) {
        userService.updateLocation(user, req);
        return ResponseEntity.noContent().build();
    }
}
