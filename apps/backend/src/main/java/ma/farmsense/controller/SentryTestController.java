package ma.farmsense.controller;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentryTestController {

    @GetMapping("/api/sentry-test")
    public String testSentry() {
        try {
            throw new Exception("This is a test endpoint to capture an exception in Sentry.");
        } catch (Exception e) {
            Sentry.captureException(e);
            return "Exception captured and sent to Sentry!";
        }
    }
}
