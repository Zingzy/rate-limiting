package com.ratelimiter.factory;

import com.ratelimiter.model.RateLimitConfig;
import com.ratelimiter.strategy.RateLimitStrategy;

// Creates a RateLimitStrategy from a config. (Factory pattern)
public interface RateLimitStrategyFactory {
    RateLimitStrategy create(RateLimitConfig config);
}
