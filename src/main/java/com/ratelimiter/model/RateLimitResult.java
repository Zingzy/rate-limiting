package com.ratelimiter.model;

// Result of a rate limit check.
public class RateLimitResult {

    private final boolean allowed;
    private final int remainingRequests;

    public RateLimitResult(boolean allowed, int remainingRequests) {
        this.allowed = allowed;
        this.remainingRequests = remainingRequests;
    }

    public boolean isAllowed() { return allowed; }
    public int getRemainingRequests() { return remainingRequests; }
}
