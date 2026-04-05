package com.ratelimiter.strategy;

import com.ratelimiter.model.RateLimitConfig;
import com.ratelimiter.model.RateLimitResult;

import java.util.concurrent.ConcurrentHashMap;

// Sliding window: smooths out the boundary burst problem of fixed window.
// Uses weighted count from previous + current window based on elapsed time.
// More accurate than fixed window, slightly more state to track.
public class SlidingWindowCounter implements RateLimitStrategy {

    private final RateLimitConfig config;
    private final ConcurrentHashMap<String, WindowState> windows = new ConcurrentHashMap<>();

    public SlidingWindowCounter(RateLimitConfig config) {
        this.config = config;
    }

    @Override
    public synchronized RateLimitResult tryConsume(String key) {
        long now = System.currentTimeMillis();
        long windowSize = config.getWindowSizeMs();
        long currentWindowStart = (now / windowSize) * windowSize;

        WindowState state = windows.computeIfAbsent(key, k -> new WindowState());

        // Roll forward if we moved to a new window
        if (state.currentWindowStart != currentWindowStart) {
            state.previousCount = (state.currentWindowStart == currentWindowStart - windowSize)
                    ? state.currentCount : 0;
            state.currentCount = 0;
            state.currentWindowStart = currentWindowStart;
        }

        // Weight of previous window = how much of it still overlaps
        double elapsedRatio = (double) (now - currentWindowStart) / windowSize;
        double previousWeight = 1.0 - elapsedRatio;
        double estimatedCount = (state.previousCount * previousWeight) + state.currentCount;

        if (estimatedCount >= config.getMaxRequests()) {
            int remaining = 0;
            return new RateLimitResult(false, remaining);
        }

        state.currentCount++;
        int remaining = (int) (config.getMaxRequests() - estimatedCount - 1);
        return new RateLimitResult(true, Math.max(0, remaining));
    }

    private static class WindowState {
        long currentWindowStart;
        int currentCount;
        int previousCount;
    }
}
