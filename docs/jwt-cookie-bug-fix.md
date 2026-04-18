# JWT Cookie 관련 버그 수정 기록

## 배경

Refresh Token 재발급 기능(`GET /auth/refresh`)이 동작하지 않는 원인을 분석하는 과정에서
세 가지 버그가 동시에 존재함을 발견했다.
각각이 독립적으로도 치명적이며, 세 버그가 겹쳐 재발급 기능 전체가 무력화된 상태였다.

---

## 버그 1. `Set-Cookie` 헤더 오독

### 원인

토큰 재발급 시 클라이언트가 보낸 Refresh Token을 읽을 때
응답 전용 헤더인 `Set-Cookie`를 요청 헤더로 읽고 있었다.

```java
// 수정 전 — JwtServiceImpl.java
String refreshToken = httpServletRequest.getHeader("Set-Cookie");
```

`Set-Cookie`는 **서버 → 브라우저** 방향의 응답 헤더다.
브라우저는 쿠키를 전송할 때 `Cookie` 헤더를 사용하므로,
위 코드는 항상 `null`을 반환했다.

### 수정

`HttpServletRequest.getCookies()`를 사용해 쿠키를 올바르게 읽도록 변경했다.

```java
// 수정 후 — JwtServiceImpl.java
private String extractCookieValue(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;
    return Arrays.stream(cookies)
        .filter(c -> name.equals(c.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
}
```

---

## 버그 2. Cookie `maxAge` 잘못된 값 사용

### 원인

Refresh Token 쿠키를 발급할 때 만료 시간으로 Access Token의 `maxAge` 값을 사용하고 있었다.

```java
// 수정 전 — JwtServiceImpl.java
ResponseCookie.from("refreshToken", refreshToken)
    .maxAge(jwtProperties.getAccessTokenMaxAge())  // 600 → 10분
```

`application-jwt.yml` 설정값:

```yaml
access:
  maxage: 600              # Access Token 쿠키 maxAge (10분, 초 단위)
refresh:
  expiration: 1209600000   # Refresh Token 만료 (14일, 밀리초 단위)
```

결과적으로 다음과 같은 불일치가 발생했다.

| 항목 | 값 |
|---|---|
| Refresh Token JWT 만료 | 14일 |
| Redis TTL | 14일 |
| 브라우저 쿠키 만료 | **10분** ← 버그 |

브라우저가 10분 후 쿠키를 삭제하면, 서버로 Refresh Token을 보낼 수 없어
Redis에 토큰이 남아 있어도 재발급이 불가능해졌다.

### 수정

Refresh Token 만료 시간을 초 단위로 변환해 쿠키 `maxAge`에 적용했다.

```java
// 수정 후 — JwtServiceImpl.java
long maxAgeSeconds = jwtProperties.getRefreshTokenExpirationPeriod() / 1000; // ms → 초

ResponseCookie.from("refreshToken", refreshToken)
    .maxAge(maxAgeSeconds)  // 1209600초 = 14일
```

---

## 버그 3. 재발급 엔드포인트 경로 불일치

### 원인

프론트엔드가 호출하는 경로와 백엔드에 매핑된 경로가 달랐다.

```
프론트엔드 (api/Auth/index.ts): GET /auth/refresh
백엔드 (JwtController.java):   GET /login/reissueToken
```

요청이 백엔드에 도달하지 못했고, `/login/reissueToken`은 호출되지 않았다.
또한 `/auth/**`는 `SecurityConfig`에서 인증 필요로 설정되어 있어
Access Token이 만료된 상황에서는 접근 자체가 차단됐다.

### 수정

백엔드 경로를 프론트엔드 호출 경로에 맞추고,
`SecurityConfig`에 해당 경로를 인증 없이 접근 가능하도록 추가했다.

```java
// 수정 전 — JwtController.java
@GetMapping("/login/reissueToken")

// 수정 후
@GetMapping("/auth/refresh")
```

```java
// SecurityConfig.java
.antMatchers("/login/**").permitAll()
.antMatchers("/auth/refresh").permitAll()  // 추가
.antMatchers("/api/**").authenticated()
```

---

## 영향 범위 요약

| 버그 | 증상 | 심각도 |
|---|---|---|
| `Set-Cookie` 헤더 오독 | Refresh Token을 읽지 못해 재발급 항상 실패 | 높음 |
| Cookie `maxAge` 오설정 | 10분 후 쿠키 삭제 → 이후 재발급 불가 | 높음 |
| 엔드포인트 경로 불일치 | 프론트 요청이 백엔드에 도달하지 않음 | 높음 |

세 버그가 동시에 존재했기 때문에 Refresh Token 재발급 기능은 완전히 동작하지 않는 상태였다.

---

## 함께 진행한 개선 사항

버그 수정과 함께 Redis 저장 구조도 개선했다.

### 기존 구조 (String)

```
KEY   = {refreshToken JWT 전체 문자열}   # ~300자
VALUE = {userId}
TTL   = 없음 (영구 저장)                 # 버그
```

- 유저 ID로 역조회 불가 → 강제 로그아웃 구현 불가
- TTL 미설정으로 3년치 만료 토큰이 Redis에 누적됨

### 개선된 구조 (Hash + TTL)

```
KEY    = "refresh:{userId}"
FIELD  = {deviceId (UUID)}
VALUE  = {refreshToken JWT}
TTL    = 14일
```

- 유저 ID 기반 O(1) 조회
- `HGETALL refresh:{userId}` 로 로그인 중인 전체 기기 확인
- `HDEL refresh:{userId} {deviceId}` 로 특정 기기만 로그아웃
- `DEL refresh:{userId}` 로 전체 기기 강제 로그아웃 (계정 탈취 대응)
- 각 디바이스의 토큰이 독립적으로 관리됨

> **주의**: Redis Hash는 키 단위로 TTL이 설정된다.
> 어느 기기에서든 토큰을 갱신하면 전체 Hash 키의 TTL이 연장된다.
> 기기별 독립 TTL이 필요하다면 `refresh:{userId}:{deviceId}` 형태의 String 키로 전환을 고려한다.
