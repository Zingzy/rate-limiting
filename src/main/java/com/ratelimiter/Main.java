package com.ratelimiter;

import com.ratelimiter.factory.RateLimitStrategyFactory;
import com.ratelimiter.model.RateLimitConfig;
import com.ratelimiter.model.RateLimitResult;
import com.ratelimiter.strategy.FixedWindowCounter;
import com.ratelimiter.strategy.SlidingWindowCounter;

public class Main {

    public static void main(String[] args) {
        // 5 requests per minute
        RateLimitConfig config = new RateLimitConfig(5, 60_000);

        System.out.println("=== Fixed Window Counter ===");
        runDemo(FixedWindowCounter::new, config);

        System.out.println("\n=== Sliding Window Counter ===");
        runDemo(SlidingWindowCounter::new, config);
    }

    // Switching algorithm = just passing a different factory. Business logic stays the same.
    private static void runDemo(RateLimitStrategyFactory factory, RateLimitConfig config) {
        RateLimiter limiter = new RateLimiter(factory.create(config));

        String tenant = "tenant-T1";

        for (int i = 1; i <= 7; i++) {
            boolean needsExternalCall = (i % 2 != 0); // odd requests need external call

            if (!needsExternalCall) {
                System.out.println("Request " + i + " | No external call needed, skipping rate limit");
                continue;
            }

            RateLimitResult result = limiter.tryConsume(tenant);
            if (result.isAllowed()) {
                System.out.println("Request " + i + " | ALLOWED | remaining: " + result.getRemainingRequests());
            } else {
                System.out.println("Request " + i + " | DENIED  | remaining: " + result.getRemainingRequests());
            }
        }
    }
}
