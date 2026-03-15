package ma.farmsense.controller;

import ma.farmsense.dto.weather.WeatherResponse;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@AuthenticationPrincipal User user) {
        if (user.getLatitude() == null || user.getLongitude() == null) {
            throw AppException.badRequest("Location not set. Please set your location first.");
        }

        WeatherResponse weather = weatherService.getWeather(
                user.getLatitude(), user.getLongitude(), user.getLang());

        return ResponseEntity.ok(weather);
    }
}
