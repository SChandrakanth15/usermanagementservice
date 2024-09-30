package com.theelixrlabs.UserManagementService.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final WebClient webClient;
    @Getter
    private String currentUser;
    @Getter
    private String currentToken;

    public JwtTokenFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://exr-138-authservice.nicepebble-15cceb5b.southindia.azurecontainerapps.io").build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            //System.out.println(jwtToken);
            // Call Auth Service to verify the token and get the username
            String username = webClient.post()
                    .uri("/auth/verify")
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();  // Blocking to ensure the username is retrieved before proceeding

            if (username != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                currentUser = username;
                currentToken = jwtToken;
            }
        }

        filterChain.doFilter(request, response);
    }
}
