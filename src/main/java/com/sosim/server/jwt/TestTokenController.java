package com.sosim.server.jwt;

import com.sosim.server.jwt.dao.JwtDao;
import com.sosim.server.jwt.property.JwtProperties;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Profile("local")
public class TestTokenController {

    private final JwtFactory jwtFactory;
    private final JwtDao jwtDao;
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;

    @GetMapping("/login/test/token")
    public ResponseEntity<?> issueTestToken(
        @RequestParam(defaultValue = "1") String userId,
        HttpServletResponse response
    ) {
        String deviceId = UUID.randomUUID().toString();
        String accessToken = jwtFactory.createAccessToken(userId);
        String refreshToken = jwtFactory.createRefreshToken(userId);
        Duration ttl = Duration.ofMillis(jwtProperties.getRefreshTokenExpirationPeriod());

        jwtDao.saveRefreshToken(userId, deviceId, refreshToken, ttl);

        RefreshToken rt = RefreshToken.builder()
            .refreshToken(refreshToken)
            .id(userId)
            .deviceId(deviceId)
            .build();
        jwtService.sendTokenCookies(response, rt);

        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken,
            "deviceId", deviceId,
            "redisTtlSeconds", String.valueOf(ttl.getSeconds()),
            "redisKey", "refresh:" + userId
        ));
    }

    @GetMapping("/login/test/devices")
    public ResponseEntity<?> listDevices(@RequestParam(defaultValue = "1") String userId) {
        return ResponseEntity.ok(jwtDao.getAllDevices(userId));
    }
}
