package com.sosim.server.jwt;

import static com.sosim.server.jwt.constant.CustomConstant.DEVICE_ID;
import static com.sosim.server.jwt.constant.CustomConstant.NONE;
import static com.sosim.server.jwt.constant.CustomConstant.REFRESH_TOKEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosim.server.config.exception.CustomException;
import com.sosim.server.jwt.dao.JwtDao;
import com.sosim.server.jwt.dto.ReIssueTokenInfo;
import com.sosim.server.jwt.property.JwtProperties;
import com.sosim.server.security.AuthUser;
import com.sosim.server.type.CodeType;
import com.sosim.server.user.User;
import com.sosim.server.user.UserRepository;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final JwtFactory jwtFactory;
    private final JwtProvider jwtProvider;
    private final JwtDao jwtDao;
    private final ObjectMapper objectMapper;

    @Override
    public void saveRefreshToken(RefreshToken refreshToken) {
        Duration ttl = Duration.ofMillis(jwtProperties.getRefreshTokenExpirationPeriod());
        jwtDao.saveRefreshToken(refreshToken.getId(), refreshToken.getDeviceId(),
            refreshToken.getRefreshToken(), ttl);
    }

    @Override
    public ReIssueTokenInfo verifyRefreshTokenAndReIssueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookieValue(request, REFRESH_TOKEN);
        String deviceId = extractCookieValue(request, DEVICE_ID);

        if (refreshToken == null || deviceId == null) {
            throw new CustomException(CodeType.NOT_FOUND_REFRESH_TOKEN);
        }

        String userId = jwtProvider.extractIdFromRefreshToken(refreshToken)
            .orElseThrow(() -> new CustomException(CodeType.INVALID_REFRESH_TOKEN));

        String storedToken = jwtDao.getRefreshToken(userId, deviceId);
        if (storedToken == null || !storedToken.equals(refreshToken) || !jwtProvider.isTokenValid(refreshToken)) {
            throw new CustomException(CodeType.INVALID_REFRESH_TOKEN);
        }

        userRepository.findById(Long.parseLong(userId))
            .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_USER));

        jwtDao.deleteRefreshToken(userId, deviceId);
        RefreshToken reIssued = jwtProvider.reIssueRefreshToken(userId, deviceId);
        sendTokenCookies(response, reIssued);

        log.info("토큰 재발급 완료 - userId: {}, deviceId: {}", userId, reIssued.getDeviceId());
        return ReIssueTokenInfo.builder().accessToken(jwtFactory.createAccessToken(userId)).build();
    }

    @Override
    public void sendTokenCookies(HttpServletResponse response, RefreshToken refreshToken) {
        long maxAgeSeconds = jwtProperties.getRefreshTokenExpirationPeriod() / 1000;

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN, refreshToken.getRefreshToken())
            .maxAge(maxAgeSeconds)
            .httpOnly(true)
            .secure(true)
            .sameSite(NONE)
            .path("/")
            .build();

        ResponseCookie deviceCookie = ResponseCookie.from(DEVICE_ID, refreshToken.getDeviceId())
            .maxAge(maxAgeSeconds)
            .httpOnly(true)
            .secure(true)
            .sameSite(NONE)
            .path("/")
            .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());
        response.addHeader("Set-Cookie", deviceCookie.toString());
    }

    @Override
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        jwtProvider.extractAccessToken(request)
            .filter(jwtProvider::isTokenValid)
            .ifPresent(accessToken -> jwtProvider.extractId(accessToken)
                .ifPresent(id -> userRepository.findById(Long.parseLong(id))
                    .ifPresent(this::saveAuthentication)));

        filterChain.doFilter(request, response);
    }

    @Override
    public void saveAuthentication(User user) {
        AuthUser context = AuthUser.builder().id(String.valueOf(user.getId())).build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(context, null, context.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public void deleteRefreshToken(HttpServletRequest httpServletRequest) {
        String refreshToken = extractCookieValue(httpServletRequest, REFRESH_TOKEN);
        String deviceId = extractCookieValue(httpServletRequest, DEVICE_ID);
        if (refreshToken == null || deviceId == null) {
            return;
        }

        jwtProvider.extractIdFromRefreshToken(refreshToken)
            .ifPresent(userId -> jwtDao.deleteRefreshToken(userId, deviceId));
    }

    private String extractCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
            .filter(c -> name.equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }
}
