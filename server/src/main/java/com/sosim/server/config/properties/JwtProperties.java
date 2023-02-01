package com.sosim.server.config.properties;

public interface JwtProperties {

    String SECRET = "justSecret"; // 우리 서버만 알고 있는 비밀값
    int EXPIRATION_TIME = 60000 * 6 * 10; // 1시간
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
