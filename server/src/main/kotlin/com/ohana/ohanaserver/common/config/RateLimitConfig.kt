package com.ohana.ohanaserver.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.ohana.ohanaserver.common.exception.ErrorResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Configuration
class RateLimitConfig(
    @Value("\${ohana.rate-limit.enabled:true}") private val enabled: Boolean,
    @Value("\${ohana.rate-limit.auth-per-minute:60}") private val authPerMinute: Int
) {
    @Bean
    fun authRateLimitFilter(objectMapper: ObjectMapper): FilterRegistrationBean<OncePerRequestFilter> {
        val filter = RateLimitFilter(enabled, authPerMinute, objectMapper)
        return FilterRegistrationBean<OncePerRequestFilter>().apply {
            this.filter = filter
            order = Ordered.HIGHEST_PRECEDENCE + 10
        }
    }
}

private class RateLimitFilter(
    private val enabled: Boolean,
    private val limitPerMinute: Int,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    private val windowMs = TimeUnit.MINUTES.toMillis(1)
    private val counters = ConcurrentHashMap<String, RateCounter>()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI ?: return true
        return !path.startsWith("/auth/")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!enabled) {
            filterChain.doFilter(request, response)
            return
        }

        val clientIp = extractClientIp(request)
        val key = "auth:$clientIp"
        val now = System.currentTimeMillis()

        val counter = counters.compute(key) { _, existing ->
            if (existing == null || now - existing.windowStart >= windowMs) {
                RateCounter(now, 1)
            } else {
                existing.count += 1
                existing
            }
        }!!

        if (counter.count > limitPerMinute) {
            response.status = 429
            response.contentType = "application/json"
            val body = ErrorResponse("RATE_LIMITED", "Too many requests")
            response.writer.write(objectMapper.writeValueAsString(body))
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun extractClientIp(request: HttpServletRequest): String {
        val forwarded = request.getHeader("X-Forwarded-For")
        if (!forwarded.isNullOrBlank()) {
            return forwarded.split(",").first().trim()
        }
        return request.remoteAddr ?: "unknown"
    }

    private data class RateCounter(var windowStart: Long, var count: Int)
}
