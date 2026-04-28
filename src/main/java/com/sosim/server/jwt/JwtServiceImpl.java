package com.sosim.server.jwt;

import static com.sosim.server.jwt.constant.CustomConstant.REFRESH_TOKEN;
import static com.sosim.server.jwt.constant.CustomConstant.SET_COOKIE;
import static com.sosim.server.jwt.constant.CustomConstant.NONE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosim.server.jwt.dao.JwtDao;
import com.sosim.server.jwt.dto.ReIssueTokenInfo;
import com.sosim.server.jwt.property.JwtProperties;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtServiceImpl implements JwtService{
    private final JwtProperties jwtProperties;
    private final JwtFactory jwtFactory;
    private final JwtProvider jwtProvider;
    private final JwtDao jwtDao;
    private final ObjectMapper objectMapper;

    /**
     * refreshToken redis에 저장
     */
    @Override
    public void saveRefreshToken(RefreshToken refreshToken) {
        jwtDao.setValues(refreshToken.getRefreshToken(), refreshToken.getId());
    }

    /**
     *  1. 헤더에서 추출한 RefreshToken으로 redis에서 유저 정보를 탐색
     *  2. 유저가 있다면 AccessToken 생성, refreshToken 재발급 & redis에 refreshToken 업데이트
     *  3. AccessToken 문자열과 Cookie에 담아 응답 헤더에 실은 RefreshToken값 반환
     */
    @Override
    public ReIssueTokenInfo verifyRefreshTokenAndReIssueAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Cookie[] cookies = httpServletRequest.getCookies();
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN)) refreshToken = cookie.getValue();
        }

        String id = jwtDao.getValues(refreshToken);
        if (jwtProvider.checkRenewRefreshToken(refreshToken)) {
            jwtDao.deleteValues(refreshToken);
            String reIssuedRefreshToken = reIssueRefreshToken(id);
            setCookieRefreshToken(response, reIssuedRefreshToken);
        }
        return ReIssueTokenInfo.builder().accessToken(jwtFactory.createAccessToken(id)).build();
    }

    @Override
    public void setCookieRefreshToken(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite(NONE)
                .maxAge(60 * 60 * 24)
                .path("/")
                .build();

        response.addHeader(SET_COOKIE, cookie.toString());
    }

    @Override
    public void deleteRefreshToken(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) return;
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN)) refreshToken = cookie.getValue();
        }

        if (refreshToken != null) {
            jwtDao.deleteValues(refreshToken);
        }
    }

    public String reIssueRefreshToken(String id) {
        String reIssuedRefreshToken = jwtFactory.createRefreshToken();
        jwtDao.setValues(reIssuedRefreshToken, id);
        return reIssuedRefreshToken;
    }
}
