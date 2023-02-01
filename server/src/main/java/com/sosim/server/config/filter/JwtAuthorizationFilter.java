package com.sosim.server.config.filter;

import com.sosim.server.config.properties.JwtProperties;
import com.sosim.server.repository.UserRepository;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        log.info("인증이나 권한이 필요한 주소값의 요청입니다.");
        String header = request.getHeader(JwtProperties.HEADER_STRING);

        // JWT를 검증해서 정상적인 사용자인지 확인
        // 1. Bearer 달고있는 header가 있는지 확인
        if(header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            // 문제가 있는 경우 다시 필터를 타게 내버려 두고
            chain.doFilter(request, response);
            // 리턴 때린다
            return;
        }
        log.info("header : {}", header);
        // 그러면 이 아래쪽이 진행 안될것

        // 2. 헤더 값이 Authorization이면 Bearer로 replace 해 준다.
        String jwtToken = request.getHeader(JwtProperties.HEADER_STRING)
            .replace(JwtProperties.TOKEN_PREFIX, "");

        // 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
        // 내가 SecurityContext에 집적 접근해서 세션을 만들때 자동으로 UserDetailsService에 있는 loadByUsername이 호출됨.
        // 서명이 정상적으로 확인되면
        /* 수정요망
        String email = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtToken)
            // String으로 캐스티 해서 줌
            .getClaim("email").asString();

        // 서명이 정상적으로 확인되어 username안에 값이 들어오면
        if(email != null) {
            User user = userRepository.findByEmail(email);

            // 인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
            // 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장!
            PrincipalDetails principalDetails = new PrincipalDetails(user);

            // 로그인 방식이 아닌, Authentication 객체 강제로 만들기??
            // Authentication객체가 실제로 로그인 해서 만들어진 것이 아니라,
            // JWT가 서명을 통해 검증이 되서,
            // username이 있으면 토큰 서명을 통해 만들어준 객체이다.
            // ** usernmae이 null 이 아니라는 것은 사용자가 정상적으로 인증이 제대로 되었다는 거고(?)
            // 인증이 된 사용자이니 우리가 강제로 Authentication객체를 만들어도 된다??
            // !!!! session이 잘 안만들어지고 있음 !!
            Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                    principalDetails, //나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
                    null, // 패스워드는 모르니까 null 처리, 어차피 지금 인증하는게 아니니까!!
                    principalDetails.getAuthorities());

            // Authentication 객체가 만들어졌으면 SecurityContextHolder.getContext()해서 시큐리티를 저장할 수 있는 세션 공간을 찾는다.
            // 강제로 시큐리티의 세션에 접근하여 값 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 로그인이 되었으니 다시 체인을 타게 한다.
        chain.doFilter(request, response);*/
    }
}
