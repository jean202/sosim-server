package com.sosim.server.config;

import com.sosim.server.jwt.JwtProvider;
import com.sosim.server.jwt.JwtService;
import com.sosim.server.security.authentication.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // csrf
                .httpBasic().disable()
                .formLogin().disable()
                .headers().frameOptions().disable();

        http
                .cors();

        // 세션 STATELESS 설정
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 요청에 대한 권한 체크 파트
        http
                .authorizeRequests()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/auth/refresh").permitAll()
                .antMatchers("/api/**").authenticated();

        // Jwt 인증 필터
        http
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationFilter authenticationFilter() {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(jwtProvider, jwtService);
        return authenticationFilter;
    }

}
