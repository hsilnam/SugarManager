package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.SocialLoginDTO;
import kr.co.sugarmanager.userservice.entity.RoleType;
import kr.co.sugarmanager.userservice.entity.SocialType;
import kr.co.sugarmanager.userservice.entity.UserEntity;
import kr.co.sugarmanager.userservice.entity.UserRoleEntity;
import kr.co.sugarmanager.userservice.repository.UserRepository;
import kr.co.sugarmanager.userservice.util.JwtProvider;
import kr.co.sugarmanager.userservice.vo.KakaoProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final KakaoOAuthService kakaoOAuthService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public SocialLoginDTO.Response socialLogin(SocialLoginDTO.Request dto) {
        KakaoProfile userInfo = kakaoOAuthService.getUserInfoWithMobile(dto.getAccessToken());
        UserEntity userEntity = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, String.valueOf(userInfo.getId()))
                .orElse(UserEntity.builder()
                        .name(Long.toString(userInfo.getId()))
                        .nickname("kakaoUser" + userInfo.getId())
                        .socialId(Long.toString(userInfo.getId()))
                        .socialType(SocialType.KAKAO)
                        .build());

        userEntity.addRoles(Set.of(UserRoleEntity.builder()
                .role(RoleType.MEMBER)
                .build()));

        userRepository.save(userEntity);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", userEntity.getPk());
        payload.put("roles",userEntity.getRoles());

        return SocialLoginDTO.Response.builder()
                .accessToken(jwtProvider.createToken(payload))
                .refreshToken(jwtProvider.createRefreshToken())
                .build();
    }

    //임시로 client에서 소셜로그인 할 수 있도록
    @Override
    public SocialLoginDTO.Response socialLogin(String code) {
        KakaoProfile userInfo = kakaoOAuthService.getUserInfo(code);
        UserEntity userEntity = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, String.valueOf(userInfo.getId()))
                .orElse(UserEntity.builder()
                        .name(Long.toString(userInfo.getId()))
                        .nickname("kakaoUser" + userInfo.getId())
                        .socialId(Long.toString(userInfo.getId()))
                        .socialType(SocialType.KAKAO)
                        .roles(Set.of(UserRoleEntity.builder()
                                .role(RoleType.MEMBER)
                                .build()))
                        .build());

        userEntity.addRoles(Set.of(UserRoleEntity.builder()
                .role(RoleType.MEMBER)
                .build()));

        userRepository.save(userEntity);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", userEntity.getPk());
        payload.put("roles",userEntity.getRoles());

        return SocialLoginDTO.Response.builder()
                .accessToken(jwtProvider.createToken(payload))
                .refreshToken(jwtProvider.createRefreshToken())
                .build();
    }
}
