package com.sosim.server.jwt.dao;

import static com.sosim.server.jwt.constant.CustomConstant.REDIS_REFRESH_PREFIX;

import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis Hash 구조: KEY = "refresh:{userId}", FIELD = deviceId, VALUE = refreshToken JWT
 *
 * 장점:
 *  - 유저 ID 기반 O(1) 조회
 *  - HGETALL로 해당 유저의 전체 디바이스 목록 확인
 *  - DEL 한 번으로 모든 디바이스 강제 로그아웃
 *
 * 주의: EXPIRE는 Hash 키 전체에 적용됨 → 어느 디바이스에서 토큰을 갱신하면 전체 TTL이 연장됨
 */
@Slf4j
@Component
public class JwtDao {

    private final RedisTemplate<String, String> redisTemplate;

    public JwtDao(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRefreshToken(String userId, String deviceId, String refreshToken, Duration ttl) {
        try {
            String key = REDIS_REFRESH_PREFIX + userId;
            redisTemplate.opsForHash().put(key, deviceId, refreshToken);
            redisTemplate.expire(key, ttl);
        } catch (Exception e) {
            log.error("Redis saveRefreshToken 실패 - userId: {}, deviceId: {}", userId, deviceId, e);
            throw e;
        }
    }

    public String getRefreshToken(String userId, String deviceId) {
        try {
            String key = REDIS_REFRESH_PREFIX + userId;
            return (String) redisTemplate.opsForHash().get(key, deviceId);
        } catch (Exception e) {
            log.error("Redis getRefreshToken 실패 - userId: {}, deviceId: {}", userId, deviceId, e);
            throw e;
        }
    }

    public void deleteRefreshToken(String userId, String deviceId) {
        try {
            String key = REDIS_REFRESH_PREFIX + userId;
            redisTemplate.opsForHash().delete(key, deviceId);
        } catch (Exception e) {
            log.error("Redis deleteRefreshToken 실패 - userId: {}, deviceId: {}", userId, deviceId, e);
            throw e;
        }
    }

    /** 해당 유저의 모든 디바이스 강제 로그아웃 */
    public void deleteAllRefreshTokens(String userId) {
        try {
            redisTemplate.delete(REDIS_REFRESH_PREFIX + userId);
        } catch (Exception e) {
            log.error("Redis deleteAllRefreshTokens 실패 - userId: {}", userId, e);
            throw e;
        }
    }

    /** 해당 유저의 모든 디바이스 목록 조회 */
    public Map<Object, Object> getAllDevices(String userId) {
        try {
            return redisTemplate.opsForHash().entries(REDIS_REFRESH_PREFIX + userId);
        } catch (Exception e) {
            log.error("Redis getAllDevices 실패 - userId: {}", userId, e);
            throw e;
        }
    }
}
