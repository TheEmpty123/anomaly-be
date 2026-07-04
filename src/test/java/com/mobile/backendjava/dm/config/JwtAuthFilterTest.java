package com.mobile.backendjava.dm.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthFilterTest {

    private static final String SECRET = "stellar-staging-super-secret-change-me-2026";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
            + "eyJzdWIiOiJzdGVsbGFyLWZyb250ZW5kIiwicm9sZSI6IkZST05URU5EX0FQSSIsImlzcyI6InN0ZWxsYXItYXBpIiwiYXVkIjoic3RlbGxhci1iYWNrZW5kIiwiaWF0IjoxNzgzMTgxMjAwLCJleHAiOjQxMDI0MzQwMDB9."
            + "LeGWjSVzDG-N52_yMFl543BLkn9Eu6xnQuJnoitCrRU";

    @Test
    void stellarApiRequestWithoutBearerTokenReturnsUnauthorized() throws ServletException, IOException {
        JwtAuthFilter filter = newFilter(true);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stellar-api/v1/ohlcv/latest");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString())
                .isEqualTo("{\"error\":\"Unauthorized\",\"message\":\"Missing or invalid JWT token\"}");
        assertThat(filterChain.getRequest()).isNull();
    }

    @Test
    void stellarApiRequestWithValidBearerTokenContinuesFilterChain() throws ServletException, IOException {
        JwtAuthFilter filter = newFilter(true);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stellar-api/v1/ohlcv/latest");
        request.addHeader("Authorization", "Bearer " + TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isSameAs(request);
    }

    @Test
    void heatmapStreamRequestWithValidQueryTokenContinuesFilterChain() throws ServletException, IOException {
        JwtAuthFilter filter = newFilter(true);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stellar-api/v1/heatmap/stream");
        request.setParameter("token", TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isSameAs(request);
    }

    @Test
    void heatmapStreamRequestWithInvalidQueryTokenReturnsUnauthorized() throws ServletException, IOException {
        JwtAuthFilter filter = newFilter(true);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stellar-api/v1/heatmap/stream");
        request.setParameter("token", "invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(filterChain.getRequest()).isNull();
    }

    @Test
    void nonSseEndpointCannotUseQueryTokenInsteadOfBearerHeader() throws ServletException, IOException {
        JwtAuthFilter filter = newFilter(true);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stellar-api/v1/ohlcv/latest");
        request.setParameter("token", TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(filterChain.getRequest()).isNull();
    }

    @Test
    void healthEndpointBypassesJwtFilter() throws ServletException, IOException {
        JwtAuthFilter filter = newFilter(true);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isSameAs(request);
    }

    @Test
    void disabledJwtFilterBypassesStellarApiRequests() throws ServletException, IOException {
        JwtAuthFilter filter = newFilter(false);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stellar-api/v1/ohlcv/latest");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isSameAs(request);
    }

    private JwtAuthFilter newFilter(boolean enabled) {
        return new JwtAuthFilter(enabled, SECRET, "stellar-api", "stellar-backend");
    }
}
