package com.sosim.server.service;

import com.sosim.server.config.auth.PrincipalDetails;
import com.sosim.server.domain.dto.GoogleUserInfo;
import com.sosim.server.domain.dto.KakaoUserInfo;
import com.sosim.server.domain.dto.NaverUserInfo;
import com.sosim.server.domain.dto.OAuth2UserInfo;
import com.sosim.server.domain.model.User;
import com.sosim.server.repository.UserRepository;
import com.sosim.server.type.Role;
import com.sosim.server.type.SocialType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String socialType = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;

        if(socialType.equals(SocialType.KAKAO)) {
            oAuth2UserInfo = new KakaoUserInfo((Map)oAuth2User.getAttributes().get("profile"));
        } else if(socialType.equals(SocialType.NAVER)) {
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        } else if(socialType.equals(SocialType.KAKAO)) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else {
           // error 상태 코드 or 메시지
        }

        String socialId = oAuth2UserInfo.getProviderId();
        String nickname = socialType + "_" + socialId;
        String password = passwordEncoder.encode("samplePassword");
        String email = oAuth2UserInfo.getEmail();
        Role role = Role.USER;

        User userEntity = userRepository.findByEmail(email);

        if(userEntity == null) {
            userEntity = User.builder()
                .nickname(nickname)
                .password(password)
                .email(email)
                .role(role)
                .socialType(SocialType.valueOf(socialType))
                .socialId(socialId)
                .build();
            userRepository.save(userEntity);
        } else {
            // 상태코드
        }
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
