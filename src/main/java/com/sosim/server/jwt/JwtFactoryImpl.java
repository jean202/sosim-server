package com.sosim.server.jwt;

import static com.sosim.server.jwt.constant.CustomConstant.ACCESS_TOKEN_SUBJECT;
import static com.sosim.server.jwt.constant.CustomConstant.ID;
import static com.sosim.server.jwt.constant.CustomConstant.REFRESH_TOKEN_SUBJECT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sosim.server.jwt.property.JwtProperties;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtFactoryImpl implements JwtFactory {

    private final JwtProperties jwtProperties;

    @Override
    public String createAccessToken(String id) {
        Date now = new Date();
        return JWT.create()
            .withSubject(ACCESS_TOKEN_SUBJECT)
            .withExpiresAt(new Date(now.getTime() + jwtProperties.getAccessTokenExpirationPeriod()))
            .withClaim(ID, id)
            .sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));
    }

    @Override
    public String createRefreshToken(String userId) {
        Date now = new Date();
        return JWT.create()
            .withSubject(REFRESH_TOKEN_SUBJECT)
            .withExpiresAt(new Date(now.getTime() + jwtProperties.getRefreshTokenExpirationPeriod()))
            .withClaim(ID, userId)  // userId 포함 → 재발급 시 Redis 조회 키로 사용
            .sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));
    }
}
