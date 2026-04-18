package com.sosim.server.jwt;

import com.sosim.server.common.response.Response;
import com.sosim.server.jwt.dto.ReIssueTokenInfo;
import com.sosim.server.type.CodeType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JwtController {

    private final JwtService jwtService;

    @GetMapping("/auth/refresh")
    public ResponseEntity<?> reIssueToken(HttpServletRequest request, HttpServletResponse response) {
        ReIssueTokenInfo reIssueTokenInfo = jwtService.verifyRefreshTokenAndReIssueAccessToken(request, response);
        return ResponseEntity.ok(Response.create(CodeType.RE_ISSUE_TOKEN, reIssueTokenInfo));
    }
}
