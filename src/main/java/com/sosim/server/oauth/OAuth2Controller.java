package com.sosim.server.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sosim.server.common.response.Response;
import com.sosim.server.jwt.JwtService;
import com.sosim.server.oauth.dto.response.LoginResponse;
import com.sosim.server.type.CodeType;
import com.sosim.server.type.SocialType;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/{socialType}")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> login(@PathVariable("socialType") String socialType, @RequestParam("code") String code,
                                   HttpServletResponse response) throws JsonProcessingException {
        LoginResponse loginResponse = oAuth2Service.login(SocialType.getSocialType(socialType), code);
        CodeType successLogin = CodeType.SUCCESS_LOGIN;
        jwtService.sendTokenCookies(response, loginResponse.getRefreshTokenObj());

        return new ResponseEntity<>(Response.create(successLogin, loginResponse), successLogin.getHttpStatus());
    }
}
