package com.microservices.identity_service.config;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.microservices.identity_service.security.UserDetailsServices;
import com.microservices.identity_service.service.JwtService;

import java.util.*;
import java.io.IOException;

/**
 * Our jwt class extends OnePerRequestFilter to be executed on every http request
 * We can also implement the Filter interface (jakarta EE), but Spring gives us a OncePerRequestFilter
 class that extends the GenericFilterBean, which also implements the Filter interface.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // @Autowired
    private final JwtService jwtService;
    // @Autowired
    private final UserDetailsServices userDetailsServices; /** implementation is provided in config.ApplicationSecurityConfig */
    private static final List<String> PUBLIC_URLS = List.of(
                    "/auth/**",
                    "/swagger-ui/**",
                    "/swagger-resources/**","/v3/api-docs/**"
            );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    //
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();
        boolean skip = PUBLIC_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
        System.out.println("shouldNotFilter: " + path + " -> " + skip);
        return skip;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("JwtAuthenticationFilter triggered for: " + request.getRequestURI());
        // try to get JWT in cookie or in Authorization Header
        String jwt = jwtService.getJwtFromCookies(request);
        final String authHeader = request.getHeader("Authorization");

        if((jwt == null && (authHeader ==  null || !authHeader.startsWith("Bearer "))) ){
//
            filterChain.doFilter(request, response);
            return;
        }

        // If the JWT is not in the cookies but in the "Authorization" header
        if (jwt == null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // after "Bearer "
        }

        final String userName =jwtService.extractUserName(jwt);
        /*
           SecurityContextHolder: is where Spring Security stores the details of who is authenticated.
           Spring Security uses that information for authorization.*/

        if(StringUtils.isNotEmpty(userName)
                && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsServices.loadUserByUsername(userName);
            if(jwtService.isTokenValid(jwt, userDetails)){
                //update the spring security context by adding a new UsernamePasswordAuthenticationToken
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                context.setAuthentication(authToken);
//                SecurityContextHolder.setContext(context);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
    }




}

