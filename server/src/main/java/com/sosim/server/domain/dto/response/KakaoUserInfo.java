package com.sosim.server.domain.dto.response;

import com.sosim.server.type.SocialType;
import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
//        super(attributes);
        this.attributes = attributes;
    }

    // TODO 고민좀 해봐야함 id로 뭘 가져오는 것을 원하며 이렇게 가져오는것이 맞는지
    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return SocialType.KAKAO.getDesc();
    }

    @Override
    public String getNickname() {
//        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
//        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
//
//        if (account == null || profile == null) {
//            return null;
//        }
//
//        return (String) profile.get("nickname");
        return (String)attributes.get("nickname");
    }

    @Override
    public String getEmail() {
        return (String)attributes.get("email");
    }
}
