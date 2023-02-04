package com.sosim.server.domain.dto.response;

import com.sosim.server.type.SocialType;
import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes; // Oauth2User.getAttributes()를 받는

    // {id=, email=, name=}
    public NaverUserInfo(Map<String, Object> attributes) {
        // spuer(attributes);
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//
//        if (response == null) {
//            return null;
//        }
//        return (String) response.get("id");
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return SocialType.NAVER.getDesc();
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getNickname() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//
//        if (response == null) {
//            return null;
//        }
//        return (String) response.get("nickname");
        return (String) attributes.get("name");
    }
}
