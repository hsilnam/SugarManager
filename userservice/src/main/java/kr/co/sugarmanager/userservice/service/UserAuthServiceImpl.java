package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.RefreshDTO;
import kr.co.sugarmanager.userservice.dto.SocialLoginDTO;
import kr.co.sugarmanager.userservice.entity.*;
import kr.co.sugarmanager.userservice.exception.AccessDenyException;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.repository.RefreshTokenRepository;
import kr.co.sugarmanager.userservice.repository.UserRepository;
import kr.co.sugarmanager.userservice.util.JwtProvider;
import kr.co.sugarmanager.userservice.util.StringUtils;
import kr.co.sugarmanager.userservice.vo.KakaoProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final KakaoOAuthService kakaoOAuthService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SocialLoginDTO.Response socialLogin(SocialLoginDTO.Request dto) {
        KakaoProfile userInfo = kakaoOAuthService.getUserInfoWithMobile(dto.getAccessToken());
        return getResponse(userInfo, dto.getFcmToken());
    }

    @Transactional
    public SocialLoginDTO.Response getResponse(KakaoProfile userInfo, String fcmToken) {
        if(userInfo == null){
            return null;
        }
        Optional<UserEntity> userEntity = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, String.valueOf(userInfo.getId()));
        UserEntity user = null;
        if (userEntity.isEmpty()) {
            String tempName = "GUEST_".concat(StringUtils.generateRandomString(10));
            user = UserEntity.builder()
                    .name(tempName)
                    .nickname(tempName)
                    .email(userInfo.getKakao_account().getEmail())
                    .socialType(SocialType.KAKAO)
                    .socialId(String.valueOf(userInfo.getId()))
                    .build();

            //setting 만들기
            UserSettingEntity setting = UserSettingEntity.builder()
                    .sugarAlert(false)
                    .pokeAlert(false)
                    .challengeAlert(false)
                    .sugarAlertHour(1)
                    .fcmToken(fcmToken)
                    .build();

            //user image만들기
            UserImageEntity profile = UserImageEntity.builder()
                    .imageUrl(userInfo.getProperties().getProfile_image())
                    .build();

            //권한
            Set<UserRoleEntity> roles = Set.of(
                    UserRoleEntity.builder()
                            .role(RoleType.MEMBER)
                            .build()
            );

            user.addProfileImage(profile);
            user.addSetting(setting);
            user.addRoles(roles);

            userRepository.save(user);
        } else {
            user = userEntity.get();

            //image 업데이트
            user.getUserImage().setImageUrl(userInfo.getKakao_account().getProfile().getProfile_image_url());
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", user.getPk());
        payload.put("roles", user.getRoles().stream().map(role -> role.getRole().getValue()).collect(Collectors.toList()));

        SocialLoginDTO.Response response = SocialLoginDTO.Response.builder()
                .accessToken(jwtProvider.createToken(payload))
                .refreshToken(jwtProvider.createRefreshToken(payload))
                .build();

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .refreshToken(response.getRefreshToken())
                .id(user.getPk())
                .build();


        //redis에 저장
        refreshTokenRepository.save(refreshToken);

        return response;
    }

    //임시로 client에서 소셜로그인 할 수 있도록
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SocialLoginDTO.Response socialLogin(String code) {
        KakaoProfile userInfo = kakaoOAuthService.getUserInfo(code);
        return getResponse(userInfo, null);
    }

    @Override
    @Transactional
    public RefreshDTO.Response refreshToken(RefreshDTO.Request dto) {
        String refreshToken = dto.getRefreshToken();

        jwtProvider.validateToken(refreshToken);//refreshToken을 validate

        refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new AccessDenyException(ErrorCode.UNAUTHORIZATION_EXCEPTION));//Redis에 있는지 확인

        return RefreshDTO.Response.builder()
                .accessToken(jwtProvider.refreshToken(refreshToken))
                .build();
    }
}
