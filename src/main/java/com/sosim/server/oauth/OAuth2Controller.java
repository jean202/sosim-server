package com.sosim.server.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sosim.server.common.response.Response;
import com.sosim.server.config.exception.CustomException;
import com.sosim.server.jwt.JwtService;
import com.sosim.server.oauth.dto.response.LoginResponse;
import com.sosim.server.type.CodeType;
import com.sosim.server.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/{socialType}")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> signUp(@PathVariable("socialType") String socialType, @RequestParam("code") String code,
                                    HttpServletResponse response) throws JsonProcessingException {
        if (code == null) {
            throw new CustomException(CodeType.BINDING_ERROR);
        }

        LoginResponse loginResponse = oAuth2Service.signUp(SocialType.getSocialType(socialType), code);
        CodeType successSignUp = CodeType.SUCCESS_SIGN_UP;
        jwtService.setCookieRefreshToken(response, loginResponse.getRefreshToken());

        return new ResponseEntity<>(Response.create(successSignUp, loginResponse), successSignUp.getHttpStatus());
    }

    @GetMapping
    public ResponseEntity<?> login(@PathVariable("socialType") String socialType, @RequestParam("code") String code,
                                   HttpServletResponse response) throws JsonProcessingException {
        if (code == null) {
            throw new CustomException(CodeType.BINDING_ERROR);
        }

        LoginResponse loginResponse = oAuth2Service.login(SocialType.getSocialType(socialType), code);
        CodeType successLogin = CodeType.SUCCESS_LOGIN;
        jwtService.setCookieRefreshToken(response, loginResponse.getRefreshToken());

        return new ResponseEntity<>(Response.create(successLogin, loginResponse), successLogin.getHttpStatus());
    }
}
