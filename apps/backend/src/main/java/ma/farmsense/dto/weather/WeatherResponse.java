package ma.farmsense.dto.weather;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WeatherResponse {

    private double temperature;
    private double humidity;
    private double windSpeed;
    private int weatherCode;
    private double precipitation;
    private String weatherDescription;
    private String weatherIcon;
    private List<DailyForecast> daily;

    @Data
    @Builder
    public static class DailyForecast {
        private String date;
        private double tempMax;
        private double tempMin;
        private int weatherCode;
        private String weatherDescription;
        private String weatherIcon;
    }
}
