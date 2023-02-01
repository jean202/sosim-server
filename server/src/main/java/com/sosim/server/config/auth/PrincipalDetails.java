package com.sosim.server.config.auth;

import com.sosim.server.domain.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Data
public class PrincipalDetails implements OAuth2User {

    private User user; // 컴포지션??
    private Map<String, Object> attributes;

    // 일반 로그인 시 사용할 생성자
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // oauth 로그인 시 사용할 생성자 : attributes를 바탕으로 User를 강제로 만들 것임
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 해당 User의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().getDesc();
            }
        });
        return collect;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return (String) attributes.get("sub");
    }
}