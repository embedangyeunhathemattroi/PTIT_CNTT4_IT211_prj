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

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String h = request.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7);
            if (jwtProvider.validate(token) && !tokenBlacklistService.isBlacklisted(token)) {
                String username = jwtProvider.getUsername(token);
                UserDetails ud = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }
}
