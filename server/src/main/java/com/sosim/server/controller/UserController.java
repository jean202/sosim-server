package com.sosim.server.controller;

import com.sosim.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // TODO 우선 페이지로 넘어가는걸 확인해야 제대로 되었는지 볼 수 있음, 그다음 jwt를
//    @PostMapping("/login")
//    public String login(@RequestBody LoginProviderRequest userLoginRequest) {
//        if (userLoginRequest.getSocialType().equals(SocialType.KAKAO)){
//            return "redirect:/oauth2/authorization/kakao";
//        } else if (userLoginRequest.getSocialType().equals(SocialType.NAVER)) {
//            return "redirect:/oauth2/authorization/naver";
//        } else if (userLoginRequest.getSocialType().equals(SocialType.GOOGLE)) {
//            return "redirect:/oauth2/authorization/google";
//        } else {
//            throw new CustomException(ErrorCodeType.PROVIDER_LIST);
//        }
//    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }
}
