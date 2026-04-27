package com.petadopt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petadopt.common.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Result<Void> result = Result.error(401, "未授权访问：" + authException.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
