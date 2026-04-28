package com.sosim.server.config.exception;

import com.sosim.server.type.CodeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class CustomException extends NestedRuntimeException {

    private CodeType codeType;
    private Content content;

    public CustomException(CodeType codeType) {
        super(codeType.getMessage());
        this.codeType = codeType;
    }

    public CustomException(CodeType codeType, String field, String message) {
        this(codeType);
        content = new Content(field, message);
    }

    @Getter
    @AllArgsConstructor
    private static class Content {
        private String field;
        private String message;
    }

    public RestError toRestError() {
        String field = this.content == null ? null : this.content.getField();
        String message = this.content == null ? this.codeType.getMessage() : this.content.getMessage();
        return new RestError(field, this.codeType.getCode(), message, this.codeType.getHttpStatus());
    }

    public ResponseEntity<RestError> toResponseEntity() {
        return new ResponseEntity<>(this.toRestError(), this.codeType.getHttpStatus());
    }

    @AllArgsConstructor
    @Getter
    public static class RestError {
        private String field;
        private int code;
        private String message;
        private HttpStatus status;

    }

    public CustomException(String message, CodeType codeType) {
        this(codeType);
        content = new Content(null, message);
    }

}
