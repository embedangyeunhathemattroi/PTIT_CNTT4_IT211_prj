package ra.demo.service.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class TokenBlacklistService {

    public void blacklist(String token, LocalDateTime expiredAt) {
        // Redis disabled
    }

    public boolean isBlacklisted(String token) {
        return false;
    }
}