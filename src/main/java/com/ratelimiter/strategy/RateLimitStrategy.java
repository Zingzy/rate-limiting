package com.ratelimiter.strategy;

import com.ratelimiter.model.RateLimitResult;

// Strategy interface for rate limiting algorithms.
public interface RateLimitStrategy {
    RateLimitResult tryConsume(String key);
}
