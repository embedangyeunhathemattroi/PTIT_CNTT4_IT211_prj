package ra.demo.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ra.demo.service.token.TokenBlacklistService;
import ra.demo.security.principal.CustomUserDetailsService;

import java.io.IOException;

/**
 * ============================================================
 * JWT AUTHENTICATION FILTER
 * ============================================================
 *
 * Chức năng:
 * - Chặn tất cả request đi vào hệ thống.
 * - Kiểm tra xem request có gửi JWT token hay không.
 * - Nếu có token:
 *      + Kiểm tra token có hợp lệ hay không.
 *      + Kiểm tra token có nằm trong blacklist hay không.
 *      + Lấy username từ token.
 *      + Load thông tin user từ database.
 *      + Tạo Authentication object.
 *      + Đưa Authentication vào SecurityContext.
 *
 * Sau khi hoàn thành các bước trên:
 * Spring Security sẽ hiểu rằng user đã đăng nhập.
 *
 * Filter này kế thừa OncePerRequestFilter
 * => Mỗi request chỉ chạy filter đúng 1 lần.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * Provider xử lý JWT.
     *
     * Bao gồm các chức năng:
     * - validate token
     * - lấy username từ token
     * - kiểm tra expiration
     * - tạo token
     */
    private final JwtProvider jwtProvider;

    /**
     * Service dùng để load thông tin người dùng.
     *
     * Sau khi lấy username từ JWT,
     * hệ thống sẽ truy vấn database để lấy:
     * - username
     * - password
     * - role
     * - authorities
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * Service quản lý blacklist token.
     *
     * Khi user logout:
     * token sẽ được lưu vào blacklist.
     *
     * Nếu token nằm trong blacklist:
     * hệ thống sẽ từ chối sử dụng token đó.
     */
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Hàm chính của filter.
     *
     * Hàm này được Spring Security gọi mỗi khi
     * có request đi qua filter chain.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        /**
         * Lấy giá trị của header Authorization.
         *
         * Ví dụ:
         * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
         */
        String h = request.getHeader("Authorization");

        /**
         * Kiểm tra:
         * 1. Header Authorization có tồn tại không.
         * 2. Header có bắt đầu bằng "Bearer " hay không.
         *
         * Nếu không thỏa mãn:
         * request sẽ bỏ qua phần xác thực JWT.
         */
        if (h != null && h.startsWith("Bearer ")) {

            /**
             * Cắt bỏ chuỗi "Bearer "
             * để lấy JWT token thực tế.
             *
             * Ví dụ:
             *
             * Bearer abc.xyz.123
             *
             * Sau khi substring(7):
             *
             * abc.xyz.123
             */
            String token = h.substring(7);

            /**
             * Kiểm tra token có hợp lệ hay không.
             *
             * validate(token) thường kiểm tra:
             * - chữ ký số (signature)
             * - thời gian hết hạn
             * - cấu trúc token
             *
             * Đồng thời kiểm tra token
             * có nằm trong blacklist hay không.
             */
            if (jwtProvider.validate(token)
                    && !tokenBlacklistService.isBlacklisted(token)) {

                /**
                 * Lấy username từ token.
                 *
                 * Ví dụ:
                 *
                 * {
                 *    "sub":"admin"
                 * }
                 *
                 * => username = admin
                 */
                String username = jwtProvider.getUsername(token);

                /**
                 * Tải thông tin người dùng từ database.
                 *
                 * Kết quả trả về UserDetails chứa:
                 * - username
                 * - password
                 * - authorities
                 * - account status
                 */
                UserDetails ud =
                        userDetailsService.loadUserByUsername(username);

                /**
                 * Tạo đối tượng Authentication.
                 *
                 * Đây là đối tượng mà Spring Security
                 * sử dụng để xác định user hiện tại.
                 *
                 * principal:
                 *     thông tin người dùng.
                 *
                 * credentials:
                 *     password.
                 *     Với JWT đã xác thực nên để null.
                 *
                 * authorities:
                 *     danh sách quyền của user.
                 */
                var auth =
                        new UsernamePasswordAuthenticationToken(
                                ud,
                                null,
                                ud.getAuthorities()
                        );

                /**
                 * Gắn thêm thông tin request.
                 *
                 * Ví dụ:
                 * - địa chỉ IP
                 * - session id
                 * - request details
                 */
                auth.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                /**
                 * Đưa Authentication vào SecurityContext.
                 *
                 * Đây là bước QUAN TRỌNG NHẤT.
                 *
                 * Sau dòng này:
                 * Spring Security hiểu rằng
                 * user đã được xác thực.
                 *
                 * Các annotation như:
                 * @PreAuthorize
                 * @Secured
                 * hasRole()
                 *
                 * sẽ sử dụng Authentication này.
                 */
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);
            }
        }

        /**
         * Chuyển request sang filter tiếp theo.
         *
         * Nếu không gọi dòng này:
         * request sẽ bị chặn lại tại filter hiện tại.
         */
        chain.doFilter(request, response);
    }
}