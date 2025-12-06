package com.microservices.identity_service.config;


import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    /* * At the application startup, during configuration, spring security will try to look for a bean of type SecurityFilterChain
     * this bean is responsible for configuring all the HTTP security of our application
     */

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final Http401UnauthorizedEntryPoint unauthorizedEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private static final Long MAX_AGE = 3600L;
    private static final int CORS_FILTER_ORDER = -102;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{


        return http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // .authorizeHttpRequests(requests -> requests
                //         .requestMatchers("/auth/public").permitAll()
                //         .anyRequest().authenticated()
                // )
                // .oauth2Login(Customizer.withDefaults())
                // .build();
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated())
                // .httpBasic(Customizer.withDefaults())
                // .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(unauthorizedEntryPoint)
//                        .accessDeniedHandler(accessDeniedHandler)
//                )

                .build();
    }
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT));
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()));
        config.setMaxAge(MAX_AGE);
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));

        // should be set order to -100 because we need to CorsFilter before SpringSecurityFilter
        bean.setOrder(CORS_FILTER_ORDER);
        return bean;
    }
}

