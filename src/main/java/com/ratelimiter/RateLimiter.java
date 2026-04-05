package com.ratelimiter;

import com.ratelimiter.model.RateLimitResult;
import com.ratelimiter.strategy.RateLimitStrategy;

// Entry point for services. Wraps a strategy and exposes a simple allow/deny check.
public class RateLimiter {

    private final RateLimitStrategy strategy;

    public RateLimiter(RateLimitStrategy strategy) {
        this.strategy = strategy;
    }

    public RateLimitResult tryConsume(String key) {
        return strategy.tryConsume(key);
    }

    public boolean isAllowed(String key) {
        return tryConsume(key).isAllowed();
    }
}
