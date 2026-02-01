package com.backend.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import com.backend.global.app.AppConfig;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            // 정적 리소스 및 공용 페이지 허용
            .requestMatchers("/", "/index", "/home", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico",
                "/member/join", "/member/login")
            .permitAll()
            // 게시물 리스트 및 상세 페이지 허용 (UI)
            .requestMatchers(HttpMethod.GET, "/posts/list", "/posts/{id:\\d+}").permitAll()
            // API 관련 규칙 (기존 유지)
            .requestMatchers(HttpMethod.GET, "/api/*/posts/{id:\\d+}").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/*/posts").permitAll()
            // 그 외 모든 요청은 인증 필요
            .anyRequest().authenticated())
        .headers(headers -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .csrf(AbstractHttpConfigurer::disable) // 상황에 따라 활성화 권장
        .formLogin(form -> form
            .loginPage("/member/login")
            .loginProcessingUrl("/member/login")
            .defaultSuccessUrl("/")
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/member/logout")
            .logoutSuccessUrl("/")
            .permitAll())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 타임리프(UI) 방식은 세션 활용
        )
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 허용할 오리진 설정
    configuration.setAllowedOrigins(List.of(AppConfig.getSiteFrontUrl()));

    // 허용할 HTTP 메서드 설정
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

    // 자격 증명 허용 설정
    configuration.setAllowCredentials(true);

    // 허용할 헤더 설정
    configuration.setAllowedHeaders(List.of("*"));

    // CORS 설정을 소스에 등록
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);

    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
