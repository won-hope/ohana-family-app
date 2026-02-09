package com.ohana.ohanaserver.common.config

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class EnvGuards {
    @Bean
    fun devProfileGuard(environment: Environment) = ApplicationRunner {
        val isDev = environment.activeProfiles.any { it == "dev" }
        val allowDev = environment.getProperty("ohana.allow-dev-profile")?.toBoolean() ?: true
        if (isDev && !allowDev) {
            throw IllegalStateException("Dev profile is disabled. Set OHANA_ALLOW_DEV_PROFILE=true to run with dev.")
        }
    }
}
