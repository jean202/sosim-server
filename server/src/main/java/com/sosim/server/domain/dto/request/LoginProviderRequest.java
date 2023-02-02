package com.sosim.server.domain.dto.request;

import com.sosim.server.type.SocialType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginProviderRequest {

    private SocialType socialType;

}
