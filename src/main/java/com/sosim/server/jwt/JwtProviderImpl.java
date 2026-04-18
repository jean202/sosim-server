package com.sosim.server.jwt;

import static com.sosim.server.jwt.constant.CustomConstant.BEARER;
import static com.sosim.server.jwt.constant.CustomConstant.ID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sosim.server.jwt.dao.JwtDao;
import com.sosim.server.jwt.property.JwtProperties;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtProviderImpl implements JwtProvider {

    private final JwtProperties jwtProperties;
    private final JwtFactory jwtFactory;
    private final JwtDao jwtDao;

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getAccessHeader()))
            .filter(token -> token.startsWith(BEARER))
            .map(token -> token.replace(BEARER, ""));
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getRefreshHeader()))
            .filter(token -> token.startsWith(BEARER))
            .map(token -> token.replace(BEARER, ""));
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey())).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<String> extractId(String accessToken) {
        try {
            return Optional.ofNullable(
                JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey()))
                    .build()
                    .verify(accessToken)
                    .getClaim(ID)
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> extractIdFromRefreshToken(String refreshToken) {
        try {
            return Optional.ofNullable(
                JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey()))
                    .build()
                    .verify(refreshToken)
                    .getClaim(ID)
                    .asString());
        } catch (Exception e) {
            log.error("리프레시 토큰에서 ID 추출 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 새 deviceId를 생성하고, 새 refreshToken을 발급한 뒤 Redis에 저장한다.
     * 기존 deviceId의 토큰은 호출 전에 삭제해야 한다.
     */
    @Override
    public RefreshToken reIssueRefreshToken(String userId, String deviceId) {
        String newDeviceId = UUID.randomUUID().toString();
        String newRefreshToken = jwtFactory.createRefreshToken(userId);
        Duration ttl = Duration.ofMillis(jwtProperties.getRefreshTokenExpirationPeriod());
        jwtDao.saveRefreshToken(userId, newDeviceId, newRefreshToken, ttl);
        return RefreshToken.builder()
            .refreshToken(newRefreshToken)
            .id(userId)
            .deviceId(newDeviceId)
            .build();
    }
}
