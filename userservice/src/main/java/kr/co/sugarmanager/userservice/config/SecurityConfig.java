package kr.co.sugarmanager.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.sugarmanager.userservice.exception.AccessDenyException;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.filter.JwtAuthenticationFilter;
import kr.co.sugarmanager.userservice.util.APIUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry.requestMatchers("/api/v1/auth/**").permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler(new AccessDeniedHandler() {
                                    @Override
                                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                                        response.setStatus(HttpStatus.FORBIDDEN.value());
                                        response.setContentType(APPLICATION_JSON_VALUE);
                                        response.setCharacterEncoding("utf-8");
                                        ResponseEntity<APIUtils.ApiResult<APIUtils.ApiError>> error = APIUtils.error(new AccessDenyException(ErrorCode.FORBIDDEN_EXCEPTION));
                                        error.getHeaders().entrySet().stream().forEach(entry -> {
                                            String key = entry.getKey();
                                            List<String> value = entry.getValue();
                                            value.stream().forEach(v -> {
                                                response.setHeader(key, v);
                                            });
                                        });

                                        response.getWriter().write(mapper.writeValueAsString(error.getBody()));
                                    }
                                })
                                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                                    @Override
                                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                        response.setContentType(APPLICATION_JSON_VALUE);
                                        response.setCharacterEncoding("utf-8");
                                        ResponseEntity<APIUtils.ApiResult<APIUtils.ApiError>> error = APIUtils.error(new AccessDenyException(ErrorCode.UNAUTHORIZATION_EXCEPTION));
                                        error.getHeaders().entrySet().stream().forEach(entry -> {
                                            String key = entry.getKey();
                                            List<String> value = entry.getValue();
                                            value.stream().forEach(v -> {
                                                response.setHeader(key, v);
                                            });
                                        });

                                        response.getWriter().write(mapper.writeValueAsString(error.getBody()));
                                    }
                                })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
