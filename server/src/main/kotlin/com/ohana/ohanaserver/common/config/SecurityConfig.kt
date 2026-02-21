package com.ohana.ohanaserver.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.header.writers.StaticHeadersWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    @Order(1)
    fun publicFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Public endpoints: health/docs and auth entry points.
            .securityMatcher(
                "/actuator/**",
                "/swagger-ui/**",
                "/swagger-ui.html", // 혹시 몰라서 이것도 추가!
                "/v3/api-docs/**",
                "/auth/**",
                "/google/sheets/connect/callback"
            )
            .cors { it.configurationSource(corsConfigurationSource()) } // CORS 활성화
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers ->
                headers.contentTypeOptions { }
                headers.frameOptions { it.deny() }
                headers.referrerPolicy { it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER) }
                headers.addHeaderWriter(
                    StaticHeadersWriter(
                        "Permissions-Policy",
                        "geolocation=(), microphone=(), camera=()"
                    )
                )
                // 🚨 핵심 수정 부분: Swagger UI가 화면을 그릴 수 있도록 자바스크립트/CSS 허용!
                headers.contentSecurityPolicy {
                    it.policyDirectives("script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:; connect-src 'self';")
                }
            }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }

        return http.build()
    }

    @Bean
    @Order(2)
    fun securedFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) } // CORS 활성화
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers ->
                headers.contentTypeOptions { }
                headers.frameOptions { it.deny() }
                headers.referrerPolicy { it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER) }
                headers.addHeaderWriter(
                    StaticHeadersWriter(
                        "Permissions-Policy",
                        "geolocation=(), microphone=(), camera=()"
                    )
                )
                // 일반 보안 체인에서도 너무 빡빡하지 않게 기본 허용치로 변경
                headers.contentSecurityPolicy {
                    it.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self';")
                }
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/auth/**",
                        "/google/sheets/connect/callback"
                    )
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .oauth2ResourceServer { it.jwt {} }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*") // 모든 출처 허용 (보안상 필요시 특정 도메인으로 제한)
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}