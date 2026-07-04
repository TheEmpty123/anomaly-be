package com.mobile.backendjava.dm.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String STELLAR_API_PREFIX = "/stellar-api/v1/";
    private static final String STELLAR_API_ROOT = "/stellar-api/v1";
    private static final String HEATMAP_STREAM_PATH = "/stellar-api/v1/heatmap/stream";
    private static final String TOKEN_QUERY_PARAM = "token";

    private final boolean jwtEnabled;
    private final String jwtSecret;
    private final String issuer;
    private final String audience;

    public JwtAuthFilter(
            @Value("${app.security.jwt.enabled:true}") boolean jwtEnabled,
            @Value("${app.security.jwt.secret}") String jwtSecret,
            @Value("${app.security.jwt.issuer}") String issuer,
            @Value("${app.security.jwt.audience}") String audience
    ) {
        this.jwtEnabled = jwtEnabled;
        this.jwtSecret = jwtSecret;
        this.issuer = issuer;
        this.audience = audience;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !jwtEnabled
                || "/actuator/health".equals(path)
                || "/error".equals(path)
                || !(STELLAR_API_ROOT.equals(path) || path.startsWith(STELLAR_API_PREFIX));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token.isEmpty() || !isValidToken(token)) {
            writeUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null) {
            if (authorization.startsWith(BEARER_PREFIX)) {
                return authorization.substring(BEARER_PREFIX.length()).trim();
            }

            return "";
        }

        if (HEATMAP_STREAM_PATH.equals(request.getRequestURI())) {
            String token = request.getParameter(TOKEN_QUERY_PARAM);
            return token == null ? "" : token.trim();
        }

        return "";
    }

    private boolean isValidToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getAudience() != null && claims.getAudience().contains(audience);
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Missing or invalid JWT token\"}");
    }
}
