package com.ohana.ohanaserver.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.header.writers.StaticHeadersWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    // 🚨 yml에서 스웨거 아이디/비번을 가져옵니다 (코드엔 노출 안 됨!)
    @Value("\${ohana.swagger.user}") private val swaggerUser: String,
    @Value("\${ohana.swagger.password}") private val swaggerPassword: String
) {

    // 스웨거 전용 임시 계정 생성
    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User.builder()
            .username(swaggerUser)
            .password("{noop}$swaggerPassword") // 암호화 없이 비교 ({noop} 필수)
            .roles("SWAGGER")
            .build()
        return InMemoryUserDetailsManager(user)
    }

    // 🚨 Order(0): 스웨거 전용 경찰 (아이디/비번 요구)
    @Bean
    @Order(0)
    fun swaggerFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher(
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/v3/api-docs",
                "/swagger-ui.html"
            )
            .authorizeHttpRequests { auth ->
                auth.anyRequest().hasRole("SWAGGER")
            }
            .httpBasic { } // 기본 로그인 팝업 활성화
            .csrf { it.disable() }
            .headers { headers ->
                headers.contentSecurityPolicy {
                    it.policyDirectives("script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:; connect-src 'self';")
                }
            }

        return http.build()
    }

    // Order(1): 누구나 들어오는 공개 API (health check 등)
    @Bean
    @Order(1)
    fun publicFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher(
                "/actuator/**",
                "/auth/**",
                "/google/sheets/connect/callback"
            )
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers ->
                headers.contentTypeOptions { }
                headers.frameOptions { it.deny() }
                headers.referrerPolicy { it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER) }
                headers.addHeaderWriter(
                    StaticHeadersWriter("Permissions-Policy", "geolocation=(), microphone=(), camera=()")
                )
            }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }

        return http.build()
    }

    // Order(2): 진짜 앱에서 쓰는 JWT 보안 API
    @Bean
    @Order(2)
    fun securedFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers ->
                headers.contentTypeOptions { }
                headers.frameOptions { it.deny() }
                headers.referrerPolicy { it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER) }
                headers.addHeaderWriter(
                    StaticHeadersWriter("Permissions-Policy", "geolocation=(), microphone=(), camera=()")
                )
            }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt {} }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}