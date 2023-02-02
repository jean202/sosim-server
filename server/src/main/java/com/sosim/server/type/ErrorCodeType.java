package com.sosim.server.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCodeType {

    //COMMON
    COMMON_BAD_REQUEST("W0000", HttpStatus.BAD_REQUEST, "Bad request"),
    COMMON_NOT_FOUND_ID("W0001", HttpStatus.BAD_REQUEST, "Not found Id"),

    // Auth
    AUTH_INVALID_ACCESS("W2000", HttpStatus.FORBIDDEN, "Invalid auth access"),
    AUTH_VERIFICATION_EXPIRED("w2005", HttpStatus.BAD_REQUEST, "Verification expired"),

    // User
    USER_EMAIL_ALREADY_EXIST("W3003", HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다. 회원가입 되어 있는 유저입니다."),
    USER_NICKNAME_ALREADY_EXIST("w3004", HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다. 회원가입 되어 있는 유저입니다."),
    // Provider
    PROVIDER_LIST("W4001", HttpStatus.BAD_REQUEST, "카카오 로그인만 지원합니다."),
    ;

    private String code;
    private HttpStatus httpStatus;
    private String message;

}
