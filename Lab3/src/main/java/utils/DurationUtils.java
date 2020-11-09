package utils;

import java.time.Duration;
import java.time.Instant;

public final class DurationUtils {
    public static long secondsBetweenTwoInstants(Instant instant1, Instant instant2){
        Duration duration = Duration.between(instant1, instant2);
        return duration.abs().getSeconds();
    }
}
