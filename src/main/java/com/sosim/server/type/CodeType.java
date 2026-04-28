package com.sosim.server.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CodeType {
    // COMMON
    COMMON_BAD_REQUEST(2001, HttpStatus.BAD_REQUEST, "Bad request"),
    COMMON_NOT_FOUND_ID(1100, HttpStatus.NOT_FOUND, "Not found Id"),
    COMMON_NO_ELEMENT(1100, HttpStatus.NOT_FOUND, "No such element"),
    INPUT_ANY_DATA(2001, HttpStatus.BAD_REQUEST, "input any data to update"),
    INPUT_PAGE_DATA(2001, HttpStatus.BAD_REQUEST, "input page index to execute"),
    COMMON_INTERNAL_SERVER_ERROR(99, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    BINDING_ERROR(2000, HttpStatus.BAD_REQUEST, "입력값 중 검증에 실패한 값이 있습니다."),

    // Auth
    AUTH_INVALID_ACCESS(1205, HttpStatus.FORBIDDEN, "Invalid auth access"),
    AUTH_VERIFICATION_EXPIRED(1206, HttpStatus.UNAUTHORIZED, "Verification expired"),

    // Jwt
    RE_ISSUE_TOKEN(900, HttpStatus.OK, "AccessToken과 RefreshToken이 성공적으로 재발급 되었습니다."),
    EXPIRE_TOKEN(1204, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    FALSIFIED_TOKEN(1203, HttpStatus.UNAUTHORIZED, "변조된 토큰입니다."),

    // User
    USER_INFO_SUCCESS(3001, HttpStatus.OK, "회원 정보가 성공적으로 조회되었습니다."),
    USER_WITHDRAWAL_SUCCESS (3002, HttpStatus.OK,"회원 탈퇴가 성공적으로 이루어졌습니다."),
    USER_ALREADY_EXIST(1101, HttpStatus.BAD_REQUEST, "회원가입 되어 있는 사용자입니다."),
    USER_ALREADY_WITHDRAWAL(3004, HttpStatus.BAD_REQUEST, "이미 탈퇴한 사용자입니다."),
    NOT_FOUND_USER(1100, HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    INVALID_USER(3006, HttpStatus.BAD_REQUEST, "사용자 정보가 일치하지 않습니다."),
    CANNOT_WITHDRAWAL_BY_GROUP_ADMIN(1102, HttpStatus.BAD_REQUEST,"모임의 총무는 소심한 총무 서비스 회원 탈퇴를 할 수 없습니다. 총무 역할을 위임해 주세요."),
    CAN_WITHDRAWAL(3008, HttpStatus.OK,"회원 탈퇴가 가능한 상태입니다."),

    // Group - Success
    CREATE_GROUP(900, HttpStatus.CREATED, "모임이 성공적으로 생성되었습니다."),
    GET_GROUP(900, HttpStatus.OK, "모임이 성공적으로 조회되었습니다."),
    GET_PARTICIPANTS(900, HttpStatus.OK, "모임 참가자가 성공적으로 조회되었습니다."),
    MODIFY_GROUP(900, HttpStatus.OK, "모임이 성공적으로 수정되었습니다."),
    DELETE_GROUP(900, HttpStatus.OK, "모임이 성공적으로 삭제되었습니다."),
    INTO_GROUP(900, HttpStatus.CREATED, "모임에 성공적으로 참가되었습니다."),
    MODIFY_GROUP_ADMIN(900, HttpStatus.OK, "관리자가 성공적으로 변경되었습니다."),
    WITHDRAW_GROUP(900, HttpStatus.OK, "성공적으로 모임에서 탈퇴되었습니다."),
    MODIFY_NICKNAME(900, HttpStatus.OK, "성공적으로 닉네임이 수정되었습니다."),
    GET_GROUPS(900, HttpStatus.OK, "성공적으로 참가한 모임들이 조회되었습니다."),
    GET_NICKNAME(900, HttpStatus.OK, "성공적으로 닉네임이 조회되었습니다."),

    // Group - Failure
    NOT_FOUND_GROUP(1001, HttpStatus.NOT_FOUND, "해당 모임을 찾을 수 없습니다."),
    NONE_ADMIN(1002, HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다."),
    NONE_PARTICIPANT(1003, HttpStatus.NOT_FOUND, "존재하지 않는 참가자 정보입니다."),
    ALREADY_USE_NICKNAME(1004, HttpStatus.BAD_REQUEST, "모임에서 이미 사용중인 닉네임입니다."),
    NO_MORE_GROUP(1005, HttpStatus.BAD_REQUEST, "더 이상 조회할 모임이 없습니다."),
    ALREADY_INTO_GROUP(1006, HttpStatus.BAD_REQUEST, "이미 참여중인 모임입니다."),
    NONE_ZERO_PARTICIPANT(1007, HttpStatus.BAD_REQUEST, "모임에 다른 참가자가 존재합니다."),

    // Event
    EVENT_CREATE_SUCCESS(900, HttpStatus.CREATED, "상세 내역이 성공적으로 생성되었습니다."),
    EVENT_INFO_SUCCESS(900, HttpStatus.OK, "상세 내역이 성공적으로 조회되었습니다."),

    EVENT_LIST_SUCCESS(900, HttpStatus.OK, "상세 내역 목록이 성공적으로 조회되었습니다."),
    EVENT_UPDATE_SUCCESS(900, HttpStatus.OK, "상세 내역이 성공적으로 수정되었습니다."),
    EVENT_DELETE_SUCCESS(900, HttpStatus.OK, "상세 내역이 성공적으로 삭제되었습니다."),
    EVENT_PAYMENT_TYPE_CHANGE_SUCCESS(900, HttpStatus.OK, "납부 여부 상태가 성공적으로 변경되었습니다."),
    EVENT_MONTH_STATUS_SUCCESS(900, HttpStatus.OK, "월별 납부 상태가 정상적으로 조회되었습니다."),
    NOT_FOUND_EVENT(1300, HttpStatus.NOT_FOUND, "해당 상세 내역은 존재하지 않습니다."),
    INVALID_EVENT_CREATER(1002, HttpStatus.BAD_REQUEST, "상세 내역 생성은 관리자만이 가능합니다."),
    INVALID_PAYMENT_TYPE_CHANGER(4012, HttpStatus.BAD_REQUEST, "해당 상세 내역의 대상자가 아닙니다."),
    PAYMENT_TYPE_MUST_BE_NON(4013, HttpStatus.BAD_REQUEST, "팀원은 확인요청만 가능합니다."),
    INVALID_PAYMENT_TYPE_PARAMETER(4014, HttpStatus.BAD_REQUEST, "기존의 납부여부 상태로 요청할 수 없습니다."),

    // OAuth - Success
    SUCCESS_SIGN_UP(900, HttpStatus.CREATED, "회원 가입이 성공적으로 완료되었습니다."),
    SUCCESS_LOGIN(900, HttpStatus.OK, "로그인이 성공적으로 완료되었습니다."),
    SUCCESS_LOGOUT(900, HttpStatus.OK, "로그아웃이 성공적으로 완료되었습니다."),
    ;

    private int code;
    private HttpStatus httpStatus;
    private String message;

}