package com.nagarro.auth_service.service;


import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPTS = 5;
    private final Duration BLOCK_DURATION = Duration.ofMinutes(15);

    private final ConcurrentHashMap<String, Attempt> attemptsCache = new ConcurrentHashMap<>();

    public boolean isBlocked(String username) {
        Attempt attempt = attemptsCache.get(username);
        if (attempt == null) return false;

        if (attempt.attempts.get() >= MAX_ATTEMPTS) {
            if (Duration.between(attempt.firstAttempt, LocalDateTime.now()).compareTo(BLOCK_DURATION) < 0) {
                return true; // still blocked
            } else {
                attemptsCache.remove(username); // unblock after duration
                return false;
            }
        }
        return false;
    }

    public void loginFailed(String username) {
        attemptsCache.compute(username, (k, v) -> {
            if (v == null) {
                return new Attempt(1, LocalDateTime.now());
            } else {
                v.attempts.incrementAndGet();
                return v;
            }
        });
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    private static class Attempt {
        AtomicInteger attempts;
        LocalDateTime firstAttempt;
        Attempt(int attempts, LocalDateTime firstAttempt) {
            this.attempts = new AtomicInteger(attempts);
            this.firstAttempt = firstAttempt;
        }
    }
}
