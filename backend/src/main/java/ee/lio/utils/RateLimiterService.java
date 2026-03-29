package ee.lio.utils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final int refillMinutes;

    public int getCapacity() {
        return capacity;
    }

    public RateLimiterService(
            @Value("${rate-limit.capacity}") int capacity,
            @Value("${rate-limit.refill-minutes}") int refillMinutes) {
        this.capacity = capacity;
        this.refillMinutes = refillMinutes;
    }

    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip,
                this::newBucket);
    }

    private Bucket newBucket(String ip) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillIntervally(capacity,
                                Duration.ofMinutes(refillMinutes))
                        .build())
                .build();
    }

}
