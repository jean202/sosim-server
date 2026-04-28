package com.sosim.server.jwt;

import com.sosim.server.jwt.dto.ReIssueTokenInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JwtService {

    void saveRefreshToken(RefreshToken refreshToken);
    ReIssueTokenInfo verifyRefreshTokenAndReIssueAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse response);
    void setCookieRefreshToken(HttpServletResponse response, String refreshToken);
    void deleteRefreshToken(HttpServletRequest httpServletRequest);
}
