# 카카오 OAuth Status 999 에러 해결 체크리스트

## ✅ 카카오 개발자 콘솔 (https://developers.kakao.com)

### 1. 내 애플리케이션 > [앱 선택]

### 2. 앱 설정 > 플랫폼

- [ ] Web 플랫폼 추가됨
- [ ] 사이트 도메인: `http://localhost:8080` 등록됨

### 3. 제품 설정 > 카카오 로그인 > 활성화 설정

- [ ] 카카오 로그인 활성화: **ON**

### 4. 제품 설정 > 카카오 로그인 > Redirect URI

- [ ] Redirect URI: `http://localhost:8080/login/oauth2/code/kakao` 등록됨
- [ ] 정확히 일치하는지 확인 (슬래시, 프로토콜 등)

### 5. 제품 설정 > 카카오 로그인 > 보안

- [ ] Client Secret 코드 생성함
- [ ] 활성화 상태: **사용함**으로 설정됨
- [ ] Client Secret 값을 application-secret.yml에 입력함

### 6. 제품 설정 > 카카오 로그인 > 동의항목

- [ ] 닉네임: 설정됨 (필수/선택)
- [ ] 프로필 사진: 설정됨 (필수/선택)
- [ ] 카카오계정(이메일): 설정됨 (필수/선택)

---

## ✅ Spring Boot 설정

### application.yml

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          kakao:
            clientId: ${custom.prod.kakao.clientId}
            clientSecret: ${custom.prod.kakao.clientSecret} # ← 있어야 함
            scope: profile_nickname, profile_image, account_email
            client-name: Kakao
            authorization-grant-type: authorization_code
            redirect-uri: http://localhost:8080/login/oauth2/code/kakao
            client-authentication-method: client_secret_post # ← Secret 필요
```

### application-secret.yml

```yaml
custom:
  prod:
    kakao:
      clientId: YOUR_REST_API_KEY # ← 카카오 REST API 키
      clientSecret: YOUR_CLIENT_SECRET # ← 카카오 Client Secret
```

---

## 🔧 디버깅 방법

### 1. 로그 레벨 올리기

application.yml에 추가:

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: DEBUG
```

### 2. 브라우저 개발자 도구

- F12 → Network 탭 열기
- 카카오 로그인 클릭
- 실패한 요청 확인
- Response 탭에서 상세 에러 확인

### 3. Spring Boot 콘솔 로그

- 애플리케이션 재시작
- 로그인 시도
- 콘솔에서 OAuth2 관련 에러 확인

---

## ⚠️ 주의사항

1. **Client Secret은 절대 공개하지 마세요!**
   - GitHub에 올릴 때 application-secret.yml을 .gitignore에 추가

2. **Redirect URI는 정확히 일치해야 합니다**
   - 프로토콜 (http/https)
   - 도메인
   - 포트 번호
   - 경로 (슬래시 포함)

3. **변경 후 반드시 재시작**
   - application.yml 수정 시 Spring Boot 재시작 필요
   - 카카오 콘솔 설정 변경 시 즉시 반영됨 (재시작 불필요)
