package com.sosim.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosim.server.config.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.sosim.server.config.filter.JwtAuthenticationFilter;
import com.sosim.server.config.filter.JwtAuthenticationProcessingFilter;
import com.sosim.server.config.filter.JwtAuthorizationFilter;
import com.sosim.server.config.handler.AuthFailureHandler;
import com.sosim.server.config.handler.AuthSuccessHandler;
import com.sosim.server.config.handler.OAuth2FailureHandler;
import com.sosim.server.config.handler.OAuth2SuccessHandler;
import com.sosim.server.repository.UserRepository;
import com.sosim.server.service.CustomOAuth2UserService;
import com.sosim.server.service.JwtService;
import com.sosim.server.service.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
//    private final PrincipalOauth2UserService principalOauth2UserService;
    private final PrincipalDetailsService principalDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            .formLogin().disable()
            .httpBasic().disable()
            .csrf().disable()

            .apply(new MyCustomDsl())
            .and()

            .authorizeRequests()
            .antMatchers("/members/member/**")
            .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
            .antMatchers("/members/member/admin/**")
            .access("hasRole('ROLE_ADMIN')")
            .anyRequest().permitAll()
            .and()
            .oauth2Login()
            .successHandler(oAuth2SuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정
            .failureHandler(oAuth2FailureHandler) // 소셜 로그인 실패 시 핸들러 설정
            .userInfoEndpoint().userService(customOAuth2UserService);

        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception{
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                .addFilter(corsConfig.corsFilter())
                .addFilter(new JwtAuthenticationFilter(authenticationManager))
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
        }
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(principalDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public AuthSuccessHandler authSuccessHandler() {
        return new AuthSuccessHandler(jwtService, userRepository);
    }

    @Bean
    public AuthFailureHandler authFailureHandler() {
        return new AuthFailureHandler();
    }

    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter
            = new CustomJsonUsernamePasswordAuthenticationFilter(new ObjectMapper());
        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(authSuccessHandler());
        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(authFailureHandler());
        return customJsonUsernamePasswordAuthenticationFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(jwtService, userRepository);
        return jwtAuthenticationProcessingFilter;
    }

}