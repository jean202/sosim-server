package com.sosim.server.domain.dto.response;

import com.sosim.server.type.Role;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2UserInfo extends DefaultOAuth2User {

    private String email;
    private Role role;

    public CustomOAuth2UserInfo(Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> attributes, String nameAttributeKey,
        String email, Role role) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = role;
    }
}
