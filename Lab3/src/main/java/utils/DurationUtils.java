package utils;

import java.time.Duration;
import java.time.Instant;

public final class DurationUtils {
    private DurationUtils() {
    }

    public static long secondsBetweenTwoInstants(Instant instant1, Instant instant2) {
        Duration duration = Duration.between(instant1, instant2);
        return duration.abs().getSeconds();
    }

    public static long milliSecondsBetweenTwoInstants(Instant instant1, Instant instant2) {
        Duration duration = Duration.between(instant1, instant2);
        return duration.abs().toMillis();
    }
}
