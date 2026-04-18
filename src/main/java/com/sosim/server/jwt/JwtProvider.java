package com.sosim.server.jwt;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public interface JwtProvider {

    Optional<String> extractAccessToken(HttpServletRequest request);
    Optional<String> extractRefreshToken(HttpServletRequest request);
    Optional<String> extractId(String accessToken);
    Optional<String> extractIdFromRefreshToken(String refreshToken);
    boolean isTokenValid(String token);
    RefreshToken reIssueRefreshToken(String userId, String deviceId);
}
