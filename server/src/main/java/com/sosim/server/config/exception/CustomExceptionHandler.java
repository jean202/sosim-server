package com.sosim.server.config.exception;

import com.sosim.server.type.ErrorCodeType;
import com.sosim.server.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@ControllerAdvice
@RestController
public class CustomExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String field = bindingResult.getFieldError().getField();
        String message = String.format("%s [%s]", bindingResult.getFieldError().getDefaultMessage(), field);
        CustomException customException = new CustomException(field, message, ErrorCodeType.COMMON_BAD_REQUEST);
        return handleCustomException(customException);
    }

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<?> handleCustomException(CustomException e) {
        log.error("> {}:{}", e.getClass().getSimpleName(), JsonUtils.toString(e.toRestError()));
        return e.toResponseEntity();
    }
}
