package com.uxstudio.contacts.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { cors ->
                cors.configurationSource {
                    val config = org.springframework.web.cors.CorsConfiguration()
                    val allowedOrigin = System.getenv("ALLOWED_ORIGINS") ?: "http://localhost:3000"
                    config.allowedOrigins = listOf(allowedOrigin)
                    config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    config.allowedHeaders = listOf("*")
                    config
                }
            }
            .csrf { it.disable() }
            .headers { headers ->
                headers.frameOptions { it.disable() }
            }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

        return http.build()
    }
}