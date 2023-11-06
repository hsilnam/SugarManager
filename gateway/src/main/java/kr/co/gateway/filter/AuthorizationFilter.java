package kr.co.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.gateway.exception.*;
import kr.co.gateway.util.APIUtils;
import kr.co.gateway.util.JwtProvider;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {
    private final JwtProvider jwtProvider;
    private final AntPathMatcher antPathMatcher;
    private final ObjectMapper mapper;
    private final String TOKEN_TYPE = "Bearer ";

    public AuthorizationFilter(JwtProvider jwtProvider, AntPathMatcher antPathMatcher, ObjectMapper mapper) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
        this.antPathMatcher = antPathMatcher;
        this.mapper = mapper;
    }

    @Getter
    public static class Config {
        // Put configuration properties here
        private static String[] whiteList;//필터가 적용되지 않아도 되는 whiteList

        public Config(String[] whiteList) {
            Config.whiteList = whiteList;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String path = request.getURI().getPath();

            boolean isWhiteList = Arrays.stream(Config.whiteList)
                    .anyMatch(pattern -> antPathMatcher.match(pattern, path));
            if (!isWhiteList) {//필터가 적용되는 path라면 jwt토큰 검증
                if (!containsAuthorization(request)) {
                    //error
                    return onError(response, new UnauthorizationException(ErrorCode.UNAUTHORIZATION_EXCEPTION));
                }

                String token = extractToken(request);

                try {
                    jwtProvider.validateToken(token);
                    //error
                } catch (CustomJwtException e) {
                    return onError(response, e);
                } catch (Exception e) {
                    return onError(response, new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION));
                }

                //refresh토큰을 authorization에 보냈다면 400 error throw
                if (jwtProvider.getSubject(token).equals(JwtProvider.TokenType.REFRESH)) {
                    return onError(response, new JwtValidationException(ErrorCode.JWT_BADREQUEST_EXCEPTION));
                }

                Long id = jwtProvider.getClaims(token, "id", Long.class);
                List<String> roles = jwtProvider.getClaims(token, "roles", List.class);

                request.mutate()
                        .header("X-Authorization-Id", String.valueOf(id))
                        .header("X-Authorization-Role", roles.stream().collect(Collectors.joining(",")))
                        .build();
            }

            return chain.filter(exchange);
        };
    }

    private boolean containsAuthorization(ServerHttpRequest request) {
        return request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
    }

    private String extractToken(ServerHttpRequest request) {
        String auth = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
        if (auth.startsWith(TOKEN_TYPE)) {
            return auth.substring(TOKEN_TYPE.length());
        } else return null;
    }

    private String errorResponseMessage(CustomException e) {
        try {
            return mapper.writeValueAsString(APIUtils.error(e));
        } catch (JsonProcessingException e1) {
            return null;
        }
    }

    private Mono<Void> onError(ServerHttpResponse response, CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        String errorResponse = errorResponseMessage(exception);

        response.setStatusCode(errorCode.getStatus());
        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}