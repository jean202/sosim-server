package com.sosim.server.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google");

    public final String desc;
}