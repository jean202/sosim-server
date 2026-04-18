package com.sosim.server.jwt;

public interface JwtFactory {
    String createAccessToken(String id);
    String createRefreshToken(String userId);
}
