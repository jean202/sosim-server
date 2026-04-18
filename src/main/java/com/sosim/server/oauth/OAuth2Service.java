package com.sosim.server.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sosim.server.config.exception.CustomException;
import com.sosim.server.jwt.JwtFactory;
import com.sosim.server.jwt.JwtService;
import com.sosim.server.jwt.RefreshToken;
import com.sosim.server.oauth.dto.response.LoginResponse;
import com.sosim.server.oauth.dto.request.OAuth2TokenRequest;
import com.sosim.server.oauth.dto.request.OAuth2UserInfoRequest;
import com.sosim.server.type.CodeType;
import com.sosim.server.type.SocialType;
import com.sosim.server.user.User;
import com.sosim.server.user.UserRepository;
import com.sosim.server.user.UserService;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2Service {
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtFactory jwtFactory;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse login(SocialType socialType, String authorizationCode) throws JsonProcessingException {
        ClientRegistration type = inMemoryRepository.findByRegistrationId(socialType.name().toLowerCase());

        OAuth2TokenRequest oAuth2Token = getToken(type, authorizationCode);

        User user = getUserProfile(socialType, oAuth2Token, type);

        String userId = String.valueOf(user.getId());
        RefreshToken refreshToken = RefreshToken.builder()
                .id(userId)
                .deviceId(UUID.randomUUID().toString())
                .refreshToken(jwtFactory.createRefreshToken(userId))
                .build();
        jwtService.saveRefreshToken(refreshToken);

        return LoginResponse.create(user, jwtFactory.createAccessToken(userId), refreshToken);
    }

    private OAuth2TokenRequest getToken(ClientRegistration type, String authorizationCode) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<?> request =
                new HttpEntity<>(tokenRequest(authorizationCode, type), headers);

        RestTemplate restTemplate = new RestTemplate();
        String responseBody;
        try {
            responseBody = restTemplate.exchange(
                    type.getProviderDetails().getTokenUri(),
                    HttpMethod.POST,
                    request,
                    String.class
            ).getBody();
        } catch (HttpStatusCodeException e) {
            throw new CustomException(CodeType.COMMON_BAD_REQUEST);
        }

        return OBJECT_MAPPER.readValue(responseBody, OAuth2TokenRequest.class);
    }

    private MultiValueMap<String, String> tokenRequest(String authorizationCode, ClientRegistration type) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", authorizationCode);
        formData.add("grant_type", "authorization_code");
        formData.add("redirection_uri", type.getRedirectUri());
        formData.add("client_secret", type.getClientSecret());
        formData.add("client_id", type.getClientId());
        return formData;
    }

    private User getUserProfile(SocialType socialType, OAuth2TokenRequest token, ClientRegistration type) throws JsonProcessingException {
        Map<String, Object> userAttributes = getUserAttributes(type, token);
        OAuth2UserInfoRequest oAuth2UserInfoRequest = OAuth2UserInfoFactory.getOAuth2UserInfo(socialType, userAttributes);

        return saveOrUpdate(socialType, oAuth2UserInfoRequest);
    }

    private User saveOrUpdate(SocialType socialType, OAuth2UserInfoRequest oAuth2UserInfoRequest) {
        Optional<User> user = userRepository.findBySocialTypeAndSocialId(socialType, oAuth2UserInfoRequest.getOAuth2Id());

        if (user.isEmpty()) {
            user = Optional.ofNullable(userService.save(socialType, oAuth2UserInfoRequest));
        } else {
            user = Optional.ofNullable(userService.update(user.get(), oAuth2UserInfoRequest));
        }

        return user.get();
    }

    private Map<String, Object> getUserAttributes(ClientRegistration type, OAuth2TokenRequest token) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + token.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request =
                new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();

        String responseBody = restTemplate.exchange(
                type.getProviderDetails().getUserInfoEndpoint().getUri(),
                HttpMethod.GET,
                request,
                String.class
        ).getBody();

        return OBJECT_MAPPER.readValue(responseBody,Map.class);
    }

}
