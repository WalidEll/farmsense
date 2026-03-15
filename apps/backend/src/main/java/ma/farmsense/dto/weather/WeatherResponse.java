package ma.farmsense.dto.weather;

import java.util.List;

public record WeatherResponse(
        double temperature,
        double humidity,
        double windSpeed,
        int weatherCode,
        double precipitation,
        String weatherDescription,
        String weatherIcon,
        List<DailyForecast> daily
) {
    public record DailyForecast(
            String date,
            double tempMax,
            double tempMin,
            int weatherCode,
            String weatherDescription,
            String weatherIcon
    ) {}
}
