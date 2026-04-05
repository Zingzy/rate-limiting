package com.ratelimiter.model;

// Holds the limit and window size for rate limiting.
public class RateLimitConfig {

    private final int maxRequests;
    private final long windowSizeMs;

    public RateLimitConfig(int maxRequests, long windowSizeMs) {
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
    }

    public int getMaxRequests() { return maxRequests; }
    public long getWindowSizeMs() { return windowSizeMs; }
}
