package kr.co.sugarmanager.userservice.user.service;

import kr.co.sugarmanager.userservice.global.service.KakaoOAuthService;
import kr.co.sugarmanager.userservice.user.dto.JoinDTO;
import kr.co.sugarmanager.userservice.user.dto.LoginDTO;
import kr.co.sugarmanager.userservice.user.entity.*;
import kr.co.sugarmanager.userservice.user.dto.RefreshDTO;
import kr.co.sugarmanager.userservice.user.dto.SocialLoginDTO;
import kr.co.sugarmanager.userservice.global.exception.AccessDenyException;
import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import kr.co.sugarmanager.userservice.user.repository.RefreshTokenRepository;
import kr.co.sugarmanager.userservice.user.repository.UserRepository;
import kr.co.sugarmanager.userservice.global.util.JwtProvider;
import kr.co.sugarmanager.userservice.global.util.StringUtils;
import kr.co.sugarmanager.userservice.global.vo.KakaoProfile;
import kr.co.sugarmanager.userservice.user.vo.RoleType;
import kr.co.sugarmanager.userservice.user.vo.SocialType;
import kr.co.sugarmanager.userservice.user.vo.UserInfoValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Join;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static kr.co.sugarmanager.userservice.user.vo.UserInfoValidation.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final KakaoOAuthService kakaoOAuthService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SocialLoginDTO.Response socialLogin(SocialLoginDTO.Request dto) {
        KakaoProfile userInfo = kakaoOAuthService.getUserInfoWithMobile(dto.getAccessToken());
        return getResponse(userInfo, dto.getFcmToken());
    }

    @Transactional
    public SocialLoginDTO.Response getResponse(KakaoProfile userInfo, String fcmToken) {
        if (userInfo == null) {
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
                .userId(user.getPk())
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

    @Override
    public LoginDTO.Response login(LoginDTO.Request req) {
        String id = req.getId();
        String pw = req.getPw();

        UserEntity user = userRepository.findById(id).orElseThrow(() -> new AccessDenyException(ErrorCode.LOGIN_INFO_NOT_VALIDATE));
        if (!passwordEncoder.matches(pw, user.getPw()))
            throw new AccessDenyException(ErrorCode.LOGIN_INFO_NOT_VALIDATE);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", user.getPk());
        payload.put("roles", user.getRoles().stream().map(role -> role.getRole().getValue()).collect(Collectors.toList()));

        return LoginDTO.Response.builder()
                .accessToken(jwtProvider.createToken(payload))
                .refreshToken(jwtProvider.createRefreshToken(payload))
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public JoinDTO.Response join(JoinDTO.Request req) {
        String name = req.getName();
        String pw = req.getPw();
        String id = req.getId();
        String email = req.getEmail();
        String nickname = req.getNickname();
        String fcmToken = req.getFcmToken();

        String[] values = {name, nickname, email, id, pw};
        UserInfoValidation[] keys = {NAME, NICKNAME, EMAIL, ID, PASSWORD};
        for (int i = 0; i < keys.length; i++) {
            UserInfoValidation validation = keys[i];
            if (!validation.validate(values[i])) {
                return JoinDTO.Response.builder()
                        .success(false)
                        .id(validation.getMessage())
                        .build();
            }
        }

        UserEntity user = UserEntity.builder()
                .id(id)
                .pw(passwordEncoder.encode(pw))
                .email(email)
                .name(name)
                .nickname(nickname)
                .socialId(StringUtils.generateRandomString(250))
                .socialType(SocialType.HOME)
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

        return JoinDTO.Response.builder()
                .success(true)
                .id(id)
                .build();
    }
}
