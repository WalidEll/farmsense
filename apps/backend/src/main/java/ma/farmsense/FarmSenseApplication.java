package ma.farmsense;

import io.sentry.Sentry;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FarmSenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmSenseApplication.class, args);
    }

    @PreDestroy
    public void onShutdown() {
        Sentry.close();
    }
}
