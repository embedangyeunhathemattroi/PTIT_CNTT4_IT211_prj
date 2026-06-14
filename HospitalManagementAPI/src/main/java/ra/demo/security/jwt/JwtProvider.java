package ra.demo.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ra.demo.security.principal.CustomUserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration-ms}")
    private long accessMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(Authentication a) {

        CustomUserDetails p =
                (CustomUserDetails) a.getPrincipal();

        return build(
                p.getUsername(),
                Map.of(
                        "role", p.user().getRole().name(),
                        "id", p.user().getId()
                ),
                accessMs
        );
    }

    public String generateRefreshToken(String username) {
        return build(
                username,
                Map.of("type", "refresh"),
                refreshMs
        );
    }

    private String build(
            String subject,
            Map<String, Object> claims,
            long ms
    ) {

        Date now = new Date();

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(
                        new Date(
                                now.getTime() + ms
                        )
                )
                .signWith(key())
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Date getExpiration(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public boolean validate(String token) {

        try {

            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public LocalDateTime expirationAsLocal(String token) {

        return getExpiration(token)
                .toInstant()
                .atZone(
                        ZoneId.systemDefault()
                )
                .toLocalDateTime();
    }
}