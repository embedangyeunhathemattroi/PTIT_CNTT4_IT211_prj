package ra.demo.security.jwt;

// Thư viện dùng để tạo, đọc và xác thực JWT
import io.jsonwebtoken.Jwts;

// Thư viện tạo SecretKey từ chuỗi secret
import io.jsonwebtoken.security.Keys;

// Đọc giá trị từ application.properties hoặc application.yml
import org.springframework.beans.factory.annotation.Value;

// Đại diện cho thông tin xác thực của người dùng hiện tại
import org.springframework.security.core.Authentication;

// Đánh dấu đây là một Spring Bean
import org.springframework.stereotype.Component;

// Custom UserDetails chứa thông tin user của hệ thống
import ra.demo.security.principal.CustomUserDetails;

// Thư viện SecretKey dùng cho thuật toán ký JWT
import javax.crypto.SecretKey;

// Hỗ trợ chuyển String thành mảng byte UTF-8
import java.nio.charset.StandardCharsets;

// Kiểu ngày giờ LocalDateTime
import java.time.LocalDateTime;

// Lấy múi giờ hệ thống
import java.time.ZoneId;

// Kiểu Date của Java
import java.util.Date;

// Lưu dữ liệu dạng key-value cho claims
import java.util.Map;

/**
 * Class chịu trách nhiệm:
 * - Sinh Access Token
 * - Sinh Refresh Token
 * - Đọc thông tin từ Token
 * - Kiểm tra Token hợp lệ
 */
@Component
public class JwtProvider {

    /**
     * Lấy giá trị jwt.secret từ file cấu hình.
     *
     * Ví dụ:
     * jwt.secret=mySecretKey123456789...
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Thời gian sống của Access Token.
     *
     * Ví dụ:
     * jwt.access-expiration-ms=900000
     * => 15 phút
     */
    @Value("${jwt.access-expiration-ms}")
    private long accessMs;

    /**
     * Thời gian sống của Refresh Token.
     *
     * Ví dụ:
     * jwt.refresh-expiration-ms=604800000
     * => 7 ngày
     */
    @Value("${jwt.refresh-expiration-ms}")
    private long refreshMs;

    /**
     * Tạo SecretKey từ chuỗi secret.
     *
     * SecretKey này được dùng:
     * - ký token
     * - xác thực token
     */
    private SecretKey key() {

        // Chuyển secret thành byte[]
        // UTF-8 để tránh lỗi encoding
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Sinh Access Token.
     *
     * Access Token dùng để:
     * - truy cập API
     * - xác thực người dùng
     */
    public String generateAccessToken(Authentication a) {

        // Lấy principal từ Authentication
        CustomUserDetails p =
                (CustomUserDetails) a.getPrincipal();

        // Gọi hàm build để tạo JWT
        return build(
                // Subject của token
                p.getUsername(),

                // Claims lưu thêm thông tin
                Map.of(
                        "role", p.user().getRole().name(),
                        "id", p.user().getId()
                ),

                // Thời gian sống access token
                accessMs
        );
    }

    /**
     * Sinh Refresh Token.
     *
     * Refresh Token dùng để:
     * - lấy Access Token mới
     * - không dùng gọi API nghiệp vụ
     */
    public String generateRefreshToken(String username) {

        // Tạo refresh token
        return build(

                // Username làm subject
                username,

                // Đánh dấu đây là refresh token
                Map.of("type", "refresh"),

                // Thời gian sống refresh token
                refreshMs
        );
    }

    /**
     * Hàm dùng chung để tạo JWT.
     *
     * @param subject username
     * @param claims dữ liệu bổ sung
     * @param ms thời gian sống token
     */
    private String build(
            String subject,
            Map<String, Object> claims,
            long ms
    ) {

        // Thời điểm hiện tại
        Date now = new Date();

        // Tạo JWT
        return Jwts.builder()

                // Thêm claims
                .claims(claims)

                // Subject (username)
                .subject(subject)

                // Thời gian phát hành
                .issuedAt(now)

                // Thời gian hết hạn
                .expiration(
                        new Date(
                                now.getTime() + ms
                        )
                )

                // Ký token bằng secret key
                .signWith(key())

                // Chuyển thành chuỗi JWT
                .compact();
    }

    /**
     * Lấy username từ token.
     */
    public String getUsername(String token) {

        // Parse token
        return Jwts.parser()

                // Xác thực chữ ký
                .verifyWith(key())

                // Tạo parser
                .build()

                // Đọc token
                .parseSignedClaims(token)

                // Lấy payload
                .getPayload()

                // Lấy subject
                .getSubject();
    }

    /**
     * Lấy thời gian hết hạn của token.
     */
    public Date getExpiration(String token) {

        // Parse token
        return Jwts.parser()

                // Verify token
                .verifyWith(key())

                // Build parser
                .build()

                // Đọc token
                .parseSignedClaims(token)

                // Lấy payload
                .getPayload()

                // Lấy expiration
                .getExpiration();
    }

    /**
     * Kiểm tra token hợp lệ hay không.
     *
     * Kiểm tra:
     * - chữ ký
     * - format JWT
     * - expiration
     */
    public boolean validate(String token) {

        try {

            // Parse token
            Jwts.parser()

                    // Verify chữ ký
                    .verifyWith(key())

                    // Tạo parser
                    .build()

                    // Đọc token
                    .parseSignedClaims(token);

            // Không có lỗi => hợp lệ
            return true;

        } catch (Exception e) {

            // Có lỗi => token không hợp lệ
            return false;
        }
    }

    /**
     * Chuyển expiration Date
     * sang LocalDateTime.
     */
    public LocalDateTime expirationAsLocal(String token) {

        // Lấy expiration Date
        return getExpiration(token)

                // Chuyển sang Instant
                .toInstant()

                // Chuyển sang múi giờ hệ thống
                .atZone(
                        ZoneId.systemDefault()
                )

                // Chuyển thành LocalDateTime
                .toLocalDateTime();
    }
}