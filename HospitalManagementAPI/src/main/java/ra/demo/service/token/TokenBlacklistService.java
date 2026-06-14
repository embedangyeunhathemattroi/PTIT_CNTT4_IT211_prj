package ra.demo.service.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private static final String PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String token, LocalDateTime expiredAt) {

        if (token == null || token.isBlank() || expiredAt == null) {
            return;
        }

        Duration ttl =
                Duration.between(
                        LocalDateTime.now(),
                        expiredAt
                );

        if (!ttl.isPositive()) {

            log.warn(
                    "[REDIS] Token already expired, skip blacklist"
            );

            return;
        }

        redisTemplate.opsForValue()
                .set(
                        buildKey(token),
                        "REVOKED",
                        ttl
                );

        log.info(
                "[REDIS] Token blacklisted successfully. TTL={}s",
                ttl.toSeconds()
        );
    }

    public boolean isBlacklisted(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(
                        buildKey(token)
                )
        );
    }

    public void remove(String token) {

        if (token == null || token.isBlank()) {
            return;
        }

        redisTemplate.delete(
                buildKey(token)
        );
    }

    private String buildKey(String token) {
        return PREFIX + token;
    }
}