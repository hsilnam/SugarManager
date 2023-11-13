package kr.co.sugarmanager.userservice.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityJwtService {
    private final JwtProvider jwtProvider;

    public Authentication getAuthentication(String accessToken) {
        jwtProvider.validateToken(accessToken);

        Long id = Long.valueOf(jwtProvider.getClaims(accessToken, "id", Integer.class));
        List<String> roles = jwtProvider.getClaims(accessToken, "roles", List.class);
        UserDetails userDetails = new JwtAuthentication(id, roles);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
