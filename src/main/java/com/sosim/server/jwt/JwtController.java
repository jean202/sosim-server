package com.sosim.server.jwt;

import com.sosim.server.common.response.Response;
import com.sosim.server.jwt.dto.ReIssueTokenInfo;
import com.sosim.server.type.CodeType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class JwtController {

    private final JwtService jwtService;

    @GetMapping("/refresh")
    public ResponseEntity<?> reIssueToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ReIssueTokenInfo reIssueTokenInfo = this.jwtService.verifyRefreshTokenAndReIssueAccessToken(httpServletRequest, httpServletResponse);
        if (reIssueTokenInfo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(Response.create(CodeType.RE_ISSUE_TOKEN, reIssueTokenInfo), CodeType.RE_ISSUE_TOKEN.getHttpStatus());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) {
        jwtService.deleteRefreshToken(httpServletRequest);
        return new ResponseEntity<>(Response.create(CodeType.SUCCESS_LOGOUT, null), CodeType.SUCCESS_LOGOUT.getHttpStatus());
    }
}
