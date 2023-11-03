package kr.co.sugarmanager.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.exception.InternalServerErrorException;
import kr.co.sugarmanager.userservice.vo.KakaoProfile;
import kr.co.sugarmanager.userservice.vo.OAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoOAuthService {
    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.redirect-uri}")
    private String redirect;
    @Value("${kakao.client-secret}")
    private String secret;

    private final ObjectMapper mapper;

    public KakaoProfile getUserInfo(String code) {
        String accessToken = getAccessToken(code);
        KakaoProfile profile = getUserInfoByToken(accessToken);
        return profile;
    }

    public KakaoProfile getUserInfoWithMobile(String token) {
        return getUserInfoByToken(token);
    }

    private String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirect);
        params.add("code", code);
        params.add("client_secret", secret);

        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        OAuthToken token = null;
        try {
            token = mapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(ErrorCode.JSON_BINDING_EXCEPTION);
        }
        return token.getAccess_token();
    }

    private KakaoProfile getUserInfoByToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);
        ResponseEntity<String> resourceProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        KakaoProfile profile = null;
        try {
            profile = mapper.readValue(resourceProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(ErrorCode.JSON_BINDING_EXCEPTION);
        }
        return profile;
    }
}
