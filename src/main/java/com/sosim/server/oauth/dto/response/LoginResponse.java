package com.sosim.server.oauth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sosim.server.jwt.RefreshToken;
import com.sosim.server.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("isPermit")
    private boolean permit;

    @JsonIgnore
    private RefreshToken refreshTokenObj;

    public static LoginResponse create(User user, String accessToken, RefreshToken refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .userId(user.getId())
                .email(user.getEmail())
                .permit(user.isPermit())
                .refreshTokenObj(refreshToken)
                .build();
    }
}
