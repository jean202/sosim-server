package com.sosim.server.config.exception;

import com.sosim.server.type.ErrorCodeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomException extends NestedRuntimeException {

    private String field;
    private String message;
    @Getter
    private HttpStatus httpStatus;
    private String code;

    public CustomException(ErrorCodeType errorCodeType) {
        this(errorCodeType.getMessage(), errorCodeType.getHttpStatus(), errorCodeType.getCode());
    }

    public CustomException(String field, String message, ErrorCodeType errorCodeType) {
        this(field, message, errorCodeType.getHttpStatus(), errorCodeType.getCode());
    }

    public CustomException(String message, ErrorCodeType errorCodeType) {
        this(message, errorCodeType.getHttpStatus(), errorCodeType.getCode());
    }

    public CustomException(ErrorCodeType errorCodeType, String field) {
        this(field, errorCodeType.getMessage(), errorCodeType.getHttpStatus(), errorCodeType.getCode());
    }

    public CustomException(String message, HttpStatus httpStatus, int code) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.code = String.valueOf(code);
    }

    public CustomException(String message, HttpStatus httpStatus, String code) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public CustomException(String field, String message, HttpStatus httpStatus, String code) {
        super(message);
        this.field = field;
        this.message = message;
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public RestError toRestError() {
        return new RestError(this.field, this.code, this.message, this.httpStatus);
    }

    public ResponseEntity<RestError> toResponseEntity() {
        return new ResponseEntity<>(this.toRestError(), this.httpStatus);
    }

    @AllArgsConstructor
    @Getter
    public static class RestError {
        private String field;
        private String code;
        private String message;
        private HttpStatus status;
    }

}
