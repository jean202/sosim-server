package com.sosim.server.config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosim.server.type.ErrorCodeType;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setStatus(ErrorCodeType.AUTH_INVALID_ACCESS.getHttpStatus().value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, new CustomException(e.getMessage(), ErrorCodeType.AUTH_INVALID_ACCESS).toRestError());
        outputStream.flush();
    }
}
