package com.ohana.ohanaserver.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    @Order(1)
    fun publicFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Public endpoints: health/docs and auth entry points.
            .securityMatcher("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/auth/**")
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers ->
                headers.contentTypeOptions { }
                headers.frameOptions { it.deny() }
                headers.referrerPolicy { it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER) }
                headers.permissionsPolicy { it.policy("geolocation=(), microphone=(), camera=()") }
                headers.contentSecurityPolicy { it.policyDirectives("default-src 'none'") }
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
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { headers ->
                headers.contentTypeOptions { }
                headers.frameOptions { it.deny() }
                headers.referrerPolicy { it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER) }
                headers.permissionsPolicy { it.policy("geolocation=(), microphone=(), camera=()") }
                headers.contentSecurityPolicy { it.policyDirectives("default-src 'none'") }
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/auth/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            // Resource server for API calls authenticated with our JWT.
            .oauth2ResourceServer { it.jwt {} }

        return http.build()
    }
}
