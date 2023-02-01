package com.sosim.server.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosim.server.config.auth.PrincipalDetails;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
    // 인증 요청시에 실행되는 함수 => /login
    // login요청을 하면 로그인 시도를 위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        log.info("JwtAuthenBticationFilter.attemptAuthentication 로그인 시도중");

        ObjectMapper mapper = new ObjectMapper();

        // formLogin방식이 아니기 때문에 직접 토큰을 만들어 준다
        /* 수정 요망
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
    */
        log.info("JwtAuthenticationFilter.attemptAuthentication : 토큰 생성 완료");

        // password는 spring security가 알아서 처리해 주고, username만 체크하면 된다?

        // 2. 3.
        // 이 코드가 실행되면 PrincipalDetailsService의 loadUserByUsername() 함수가 실행됨
        // : authentication에 내 로그인한 정보가 담기게 된다
        // DB에 있는 username, password와 일치하는지 확인

        // authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
        // loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
        // UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
        // UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
        // Authentication 객체를 만들어서 필터체인으로 리턴해준다.

        // Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
        // Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
        // 결론은 인증 프로바이더에게 알려줄 필요가 없음.
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 값이 있으면 로그인 정상적으로 되었다는 뜻
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        log.info("Authentication : " + principalDetails.getUser().getNickname());

        // 4.
        // authentication객체를 session영역에 저장 => 로그인이 되었다는 뜻
        // 굳이 jwt 토큰을 사용하면서 세션을 만들 이유가 없으나 권한 처리를 해야 해서 세션에 넣어준다
        // 리턴만 해주면 권한 관리를 security가 대신 해준다

        // 5.
        // 그런데, 여기서 굳이 JWT토큰을 만들지 않아도 된다
        // ->
//        return authentication;
        return null;
    }

    // attemptAuthentication메서드가 종료된 후, 인증이 정상적으로 되었으면 그 뒤에 실행되는 메서드
    // JWT토큰을 만들어서 request요청한 사용자에게 JWT토큰을 response 해 주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authResult) throws IOException, ServletException {

        log.info("JwtAuthenticationFilter.successfulAuthentication : 인증은 완료되었다는 뜻");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        /* 수정요망
        String jwtToken = JWT.create()
            // 토큰이름
            .withSubject(principalDetails.getUsername())
            // 만료 시간
            .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
            // 비공개 클레임, 내가 넣고 싶은 값을 키-밸류 형식으 뭐든 막 넣으면 된다..?
            .withClaim("id", principalDetails.getUser().getId())
            .withClaim("username", principalDetails.getUser().getUsername())
            // 시크릿은 서버만 아는 고유한 값이어야(이 값으로 싸인을 하는것)
            .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        // 사용자에게 응답할 response 헤더에 Bearer값으로 내려줌
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

         */
    }
}
