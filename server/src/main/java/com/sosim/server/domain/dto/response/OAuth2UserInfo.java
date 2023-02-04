package com.sosim.server.domain.dto.response;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getNickname();
}

