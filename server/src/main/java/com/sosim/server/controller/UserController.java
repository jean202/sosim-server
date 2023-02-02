package com.sosim.server.controller;

import com.sosim.server.config.exception.CustomException;
import com.sosim.server.domain.dto.request.LoginProviderRequest;
import com.sosim.server.service.UserService;
import com.sosim.server.type.ErrorCodeType;
import com.sosim.server.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping("/login")
    public String login(@RequestBody LoginProviderRequest userLoginRequest) {
        if (userLoginRequest.getSocialType().equals(SocialType.KAKAO)){
            return "redirect:/oauth2/authorization/kakao";
        } else if (userLoginRequest.getSocialType().equals(SocialType.NAVER)) {
            return "redirect:/oauth2/authorization/naver";
        } else if (userLoginRequest.getSocialType().equals(SocialType.GOOGLE)) {
            return "redirect:/oauth2/authorization/google";
        } else {
            throw new CustomException(ErrorCodeType.PROVIDER_LIST);
        }
    }
}
