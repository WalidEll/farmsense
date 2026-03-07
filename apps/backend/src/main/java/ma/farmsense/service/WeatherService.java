package ma.farmsense.service;

import lombok.extern.slf4j.Slf4j;
import ma.farmsense.dto.weather.WeatherResponse;
import ma.farmsense.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@Slf4j
public class WeatherService {

    private final WebClient webClient;

    public WeatherService(
            WebClient.Builder webClientBuilder,
            @Value("${farmsense.weather.api-url}") String apiUrl) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    /**
     * Fetch current weather + 7-day forecast from Open-Meteo.
     * Cached by rounded lat/lng (2 decimal places ~ 1km).
     */
    @Cacheable(value = "weather", key = "T(java.lang.String).format('%.2f,%.2f', #latitude, #longitude)")
    public WeatherResponse getWeather(double latitude, double longitude, User.Language lang) {
        log.info("Fetching weather from Open-Meteo for lat={}, lng={}", latitude, longitude);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code,precipitation")
                            .queryParam("daily", "temperature_2m_max,temperature_2m_min,weather_code")
                            .queryParam("timezone", "auto")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Empty response from Open-Meteo");
            }

            return mapResponse(response, lang);
        } catch (Exception e) {
            log.error("Failed to fetch weather: {}", e.getMessage());
            throw new RuntimeException("Weather service unavailable", e);
        }
    }

    @SuppressWarnings("unchecked")
    private WeatherResponse mapResponse(Map<String, Object> response, User.Language lang) {
        Map<String, Object> current = (Map<String, Object>) response.get("current");

        double temperature = toDouble(current.get("temperature_2m"));
        double humidity = toDouble(current.get("relative_humidity_2m"));
        double windSpeed = toDouble(current.get("wind_speed_10m"));
        int weatherCode = toInt(current.get("weather_code"));
        double precipitation = toDouble(current.get("precipitation"));

        // Daily forecast
        Map<String, Object> daily = (Map<String, Object>) response.get("daily");
        List<String> dates = (List<String>) daily.get("time");
        List<Number> tempMaxList = (List<Number>) daily.get("temperature_2m_max");
        List<Number> tempMinList = (List<Number>) daily.get("temperature_2m_min");
        List<Number> dailyCodes = (List<Number>) daily.get("weather_code");

        List<WeatherResponse.DailyForecast> forecasts = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            int code = dailyCodes.get(i).intValue();
            forecasts.add(WeatherResponse.DailyForecast.builder()
                    .date(dates.get(i))
                    .tempMax(tempMaxList.get(i).doubleValue())
                    .tempMin(tempMinList.get(i).doubleValue())
                    .weatherCode(code)
                    .weatherDescription(wmoDescription(code, lang))
                    .weatherIcon(wmoIcon(code))
                    .build());
        }

        return WeatherResponse.builder()
                .temperature(temperature)
                .humidity(humidity)
                .windSpeed(windSpeed)
                .weatherCode(weatherCode)
                .precipitation(precipitation)
                .weatherDescription(wmoDescription(weatherCode, lang))
                .weatherIcon(wmoIcon(weatherCode))
                .daily(forecasts)
                .build();
    }

    // ── WMO Weather Interpretation Codes → emoji ─────────────────
    private String wmoIcon(int code) {
        return switch (code) {
            case 0 -> "\u2600\uFE0F";         // clear sky
            case 1, 2, 3 -> "\u26C5";          // partly cloudy
            case 45, 48 -> "\uD83C\uDF2B\uFE0F"; // fog
            case 51, 53, 55 -> "\uD83C\uDF26\uFE0F"; // drizzle
            case 56, 57 -> "\uD83E\uDDCA";     // freezing drizzle
            case 61, 63, 65 -> "\uD83C\uDF27\uFE0F"; // rain
            case 66, 67 -> "\uD83C\uDF28\uFE0F"; // freezing rain
            case 71, 73, 75 -> "\u2744\uFE0F";  // snow
            case 77 -> "\uD83C\uDF28\uFE0F";    // snow grains
            case 80, 81, 82 -> "\uD83C\uDF27\uFE0F"; // showers
            case 85, 86 -> "\uD83C\uDF28\uFE0F"; // snow showers
            case 95 -> "\u26C8\uFE0F";          // thunderstorm
            case 96, 99 -> "\u26C8\uFE0F";      // thunderstorm + hail
            default -> "\uD83C\uDF24\uFE0F";
        };
    }

    // ── WMO Weather Codes → multilingual descriptions ────────────
    private String wmoDescription(int code, User.Language lang) {
        return switch (code) {
            case 0 -> switch (lang) {
                case DARIJA -> "Sma s7a";
                case AR -> "\u0633\u0645\u0627\u0621 \u0635\u0627\u0641\u064A\u0629";
                default -> "Ciel d\u00E9gag\u00E9";
            };
            case 1, 2, 3 -> switch (lang) {
                case DARIJA -> "Chwiya d s7ab";
                case AR -> "\u063A\u0627\u0626\u0645 \u062C\u0632\u0626\u064A\u0627\u064B";
                default -> "Partiellement nuageux";
            };
            case 45, 48 -> switch (lang) {
                case DARIJA -> "Dbbab";
                case AR -> "\u0636\u0628\u0627\u0628";
                default -> "Brouillard";
            };
            case 51, 53, 55 -> switch (lang) {
                case DARIJA -> "Rchach khfif";
                case AR -> "\u0631\u0630\u0627\u0630";
                default -> "Bruine";
            };
            case 56, 57 -> switch (lang) {
                case DARIJA -> "Rchach bard";
                case AR -> "\u0631\u0630\u0627\u0630 \u0645\u062A\u062C\u0645\u062F";
                default -> "Bruine vergla\u00E7ante";
            };
            case 61, 63, 65 -> switch (lang) {
                case DARIJA -> "Shta";
                case AR -> "\u0645\u0637\u0631";
                default -> "Pluie";
            };
            case 66, 67 -> switch (lang) {
                case DARIJA -> "Shta barda";
                case AR -> "\u0645\u0637\u0631 \u0645\u062A\u062C\u0645\u062F";
                default -> "Pluie vergla\u00E7ante";
            };
            case 71, 73, 75 -> switch (lang) {
                case DARIJA -> "Ttelj";
                case AR -> "\u062B\u0644\u062C";
                default -> "Neige";
            };
            case 77 -> switch (lang) {
                case DARIJA -> "7bab ttelj";
                case AR -> "\u062D\u0628\u064A\u0628\u0627\u062A \u062B\u0644\u062C\u064A\u0629";
                default -> "Grains de neige";
            };
            case 80, 81, 82 -> switch (lang) {
                case DARIJA -> "Sh7al mn shta";
                case AR -> "\u0632\u062E\u0627\u062A \u0645\u0637\u0631\u064A\u0629";
                default -> "Averses";
            };
            case 85, 86 -> switch (lang) {
                case DARIJA -> "Ttelj ktar";
                case AR -> "\u0632\u062E\u0627\u062A \u062B\u0644\u062C\u064A\u0629";
                default -> "Averses de neige";
            };
            case 95 -> switch (lang) {
                case DARIJA -> "R3d w brek";
                case AR -> "\u0639\u0627\u0635\u0641\u0629 \u0631\u0639\u062F\u064A\u0629";
                default -> "Orage";
            };
            case 96, 99 -> switch (lang) {
                case DARIJA -> "R3d w brd";
                case AR -> "\u0639\u0627\u0635\u0641\u0629 \u0628\u0631\u062F";
                default -> "Orage avec gr\u00EAle";
            };
            default -> switch (lang) {
                case DARIJA -> "Weqt 3adi";
                case AR -> "\u0637\u0642\u0633 \u0639\u0627\u062F\u064A";
                default -> "Temps normal";
            };
        };
    }

    private double toDouble(Object val) {
        if (val instanceof Number n) return n.doubleValue();
        return 0.0;
    }

    private int toInt(Object val) {
        if (val instanceof Number n) return n.intValue();
        return 0;
    }
}
