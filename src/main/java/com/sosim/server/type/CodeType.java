package com.sosim.server.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CodeType {
    //COMMON
    COMMON_BAD_REQUEST("0000", HttpStatus.BAD_REQUEST, "Bad request"),
    COMMON_NOT_FOUND_ID("0001", HttpStatus.NOT_FOUND, "Not found Id"),
    COMMON_NO_ELEMENT("0002", HttpStatus.NOT_FOUND, "No such element"),
    BINDING_ERROR("1000", HttpStatus.BAD_REQUEST, "입력값 중 검증에 실패한 값이 있습니다."),

    // Auth
    AUTH_INVALID_ACCESS("2000", HttpStatus.FORBIDDEN, "Invalid auth access"),
    AUTH_VERIFICATION_EXPIRED("2005", HttpStatus.BAD_REQUEST, "Verification expired"),

    // User
    USER_ALREADY_EXIST("3001", HttpStatus.BAD_REQUEST, "회원가입 되어 있는 사용자입니다."),
    NOT_FOUND_USER("3002", HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    INVALID_USER("3003", HttpStatus.BAD_REQUEST, "사용자 정보가 일치하지 않습니다."),

    // Group - Success
    CREATE_GROUP("900", HttpStatus.CREATED, "모임이 성공적으로 생성되었습니다."),
    GET_GROUP("900", HttpStatus.OK, "모임이 성공적으로 조회되었습니다."),
    GET_PARTICIPANTS("900", HttpStatus.OK, "모임 참가자가 성공적으로 조회되었습니다."),
    MODIFY_GROUP("900", HttpStatus.OK, "모임이 성공적으로 수정되었습니다."),
    DELETE_GROUP("900", HttpStatus.OK, "모임이 성공적으로 삭제되었습니다."),
    INTO_GROUP("900", HttpStatus.CREATED, "모임에 성공적으로 참가되었습니다."),
    MODIFY_GROUP_ADMIN("900", HttpStatus.OK, "관리자가 성공적으로 변경되었습니다."),
    WITHDRAW_GROUP("900", HttpStatus.OK, "성공적으로 모임에서 탈퇴되었습니다."),
    MODIFY_NICKNAME("900", HttpStatus.OK, "성공적으로 닉네임이 수정되었습니다."),
    GET_GROUPS("900", HttpStatus.OK, "성공적으로 참가한 모임들이 조회되었습니다."),

    // Group - Failure
    NOT_FOUND_GROUP("1001", HttpStatus.NOT_FOUND, "해당 모임을 찾을 수 없습니다."),
    NONE_ADMIN("1002", HttpStatus.BAD_REQUEST, "관리자 권한이 필요합니다."),
    NONE_PARTICIPANT("1003", HttpStatus.NOT_FOUND, "존재하지 않는 참가자 정보입니다."),
    ALREADY_USE_NICKNAME("1004", HttpStatus.BAD_REQUEST, "모임에서 이미 사용중인 닉네임입니다."),
    NO_MORE_GROUP("1005", HttpStatus.BAD_REQUEST, "더 이상 조회할 모임이 없습니다."),
    ALREADY_INTO_GROUP("1006", HttpStatus.BAD_REQUEST, "이미 참여중인 모임입니다."),

    // OAuth - Success
    SUCCESS_LOGIN("900", HttpStatus.OK, "로그인이 성공적으로 완료되었습니다."),

    // Provider
    PROVIDER_LIST("4001", HttpStatus.BAD_REQUEST, "카카오 로그인만 지원합니다."),
    ;

    private String code;
    private HttpStatus httpStatus;
    private String message;

}
