//package com.sosim.server.service;
//
//import com.sosim.server.config.auth.PrincipalDetails;
//import com.sosim.server.config.exception.CustomException;
//import com.sosim.server.domain.dto.response.GoogleUserInfo;
//import com.sosim.server.domain.dto.response.KakaoUserInfo;
//import com.sosim.server.domain.dto.response.NaverUserInfo;
//import com.sosim.server.domain.dto.response.OAuth2UserInfo;
//import com.sosim.server.domain.model.User;
//import com.sosim.server.repository.UserRepository;
//import com.sosim.server.type.ErrorCodeType;
//import com.sosim.server.type.Role;
//import com.sosim.server.type.SocialType;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        String socialType = userRequest.getClientRegistration().getRegistrationId();
//
//        OAuth2UserInfo oAuth2UserInfo = null;
//
//        if(socialType.equals(SocialType.KAKAO)) {
//            oAuth2UserInfo = new KakaoUserInfo((Map)((Map)oAuth2User.getAttributes().get("kakao_account")).get("profile"));
//        } else if(socialType.equals(SocialType.NAVER)) {
//            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
//        } else if(socialType.equals(SocialType.GOOGLE)) {
//            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
//        } else {
//            throw new CustomException(ErrorCodeType.PROVIDER_LIST);
//        }
//
//        String socialId = oAuth2UserInfo.getProviderId();
//        String nickname = socialType + "_" + socialId;
//        // TODO 이부분 의문 해결
//        String password = passwordEncoder.encode("samplePassword");
//        String email = oAuth2UserInfo.getEmail();
//        Role role = Role.USER;
//
//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new CustomException(ErrorCodeType.USER_EMAIL_ALREADY_EXIST, userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCodeType.NOT_FOUND_EMAIL)).getEmail());
//        }
//
//        if (userRepository.findByNickname(nickname).isPresent()) {
//            throw new CustomException(ErrorCodeType.USER_NICKNAME_ALREADY_EXIST, userRepository.findByEmail(nickname).orElseThrow(() -> new CustomException(ErrorCodeType.NOT_FOUND_NICKNAME)).getNickname());
//        }
//
//        User userEntity = User.builder()
//            .nickname(nickname)
//            .password(password)
//            .email(email)
//            .role(role)
//            .socialType(SocialType.valueOf(socialType))
//            .socialId(socialId)
//            .build();
//        userRepository.save(userEntity);
//
//        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
//    }
//}
