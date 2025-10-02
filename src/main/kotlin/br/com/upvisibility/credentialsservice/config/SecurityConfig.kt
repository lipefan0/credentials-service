    package br.com.upvisibility.credentialsservice.config

    import org.springframework.context.annotation.Bean
    import org.springframework.context.annotation.Configuration
    import org.springframework.http.HttpMethod
    import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
    import org.springframework.security.config.web.server.SecurityWebFiltersOrder
    import org.springframework.security.config.web.server.ServerHttpSecurity
    import org.springframework.security.web.server.SecurityWebFilterChain
    import org.springframework.web.cors.CorsConfiguration
    import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

    @Configuration
    @EnableWebFluxSecurity
    class SecurityConfig(
        private val jwtAuthenticationWebFilter: JwtAuthenticationWebFilter
    ) {

        @Bean
        fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
            http
                .csrf { it.disable() }
                .cors { } // usa bean abaixo
                .authorizeExchange {
                    it.pathMatchers("/credentials/bling").permitAll()
                    it.pathMatchers("/credentials/internal/**").permitAll() // já filtrado por API Key no WebFilter
                    it.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    it.pathMatchers("/credentials/**").authenticated()
                    it.anyExchange().permitAll()
                }
                .build()

        @Bean
        fun corsConfigurationSource(): org.springframework.web.cors.reactive.CorsConfigurationSource {
            val config = CorsConfiguration()
            config.allowedOrigins = listOf("*")
            config.allowedMethods = listOf("GET","POST","PUT","PATCH","DELETE","OPTIONS")
            config.allowedHeaders = listOf("*")
            config.allowCredentials = false
            val source = UrlBasedCorsConfigurationSource()
            source.registerCorsConfiguration("/**", config)
            return source
        }

    }
