package kr.co.sugarmanager.userservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.sugarmanager.userservice.exception.CustomJwtException;
import kr.co.sugarmanager.userservice.util.APIUtils;
import kr.co.sugarmanager.userservice.util.SecurityJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final SecurityJwtService jwtService;
    private final String BEARER_TYPE = "Bearer";
    private final ObjectMapper mapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (token != null) {
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(jwtService.getAuthentication(token));
            }
            filterChain.doFilter(request, response);
        } catch (CustomJwtException e) {
            response.setCharacterEncoding("utf8");
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setStatus(e.getErrorCode().getStatus().value());
            ResponseEntity<APIUtils.ApiResult<APIUtils.ApiError>> error = APIUtils.error(e);
            error.getHeaders().entrySet().stream().forEach(entry -> {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                value.stream().forEach(v -> {
                    response.setHeader(key, v);
                });
            });

            response.getWriter().write(mapper.writeValueAsString(error.getBody()));
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        if (authorization != null && authorization.startsWith(BEARER_TYPE)) {
            return authorization.substring(BEARER_TYPE.length());
        }
        return null;
    }
}
