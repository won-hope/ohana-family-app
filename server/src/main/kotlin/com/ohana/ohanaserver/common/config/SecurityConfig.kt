package com.ohana.ohanaserver.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // ✅ 지금은 REST API 개발 단계 -> CSRF 끔 (Postman 호출 편하게)
            .csrf { it.disable() }

            // ✅ 세션 안 쓰는 방향(나중에 JWT로 갈거라)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            // ✅ 접근 정책
            .authorizeHttpRequests { auth ->
                auth
                    // health check
                    .requestMatchers("/actuator/**").permitAll()

                    // swagger / openapi
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                    ).permitAll()

                    .requestMatchers("/groups/**").permitAll()

                    // 나머지는 일단 막자 (추후 OAuth/JWT 붙이면 여기에 추가)
                    .anyRequest().authenticated()
            }

        return http.build()
    }
}
