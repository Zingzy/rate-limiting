package com.ratelimiter.strategy;

import com.ratelimiter.model.RateLimitConfig;
import com.ratelimiter.model.RateLimitResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Fixed window: divides time into fixed intervals and counts requests per window.
// Simple and memory-efficient, but allows burst at window boundaries (2x burst problem).
public class FixedWindowCounter implements RateLimitStrategy {

    private final RateLimitConfig config;
    private final ConcurrentHashMap<String, WindowState> windows = new ConcurrentHashMap<>();

    public FixedWindowCounter(RateLimitConfig config) {
        this.config = config;
    }

    @Override
    public RateLimitResult tryConsume(String key) {
        long currentWindow = System.currentTimeMillis() / config.getWindowSizeMs();

        WindowState state = windows.compute(key, (k, existing) -> {
            if (existing == null || existing.windowId != currentWindow) {
                return new WindowState(currentWindow);
            }
            return existing;
        });

        int count = state.counter.incrementAndGet();
        int remaining = Math.max(0, config.getMaxRequests() - count);
        boolean allowed = count <= config.getMaxRequests();

        return new RateLimitResult(allowed, remaining);
    }

    private static class WindowState {
        final long windowId;
        final AtomicInteger counter;

        WindowState(long windowId) {
            this.windowId = windowId;
            this.counter = new AtomicInteger(0);
        }
    }
}
