package kr.co.sugarmanager.userservice.integ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import kr.co.sugarmanager.userservice.global.util.JwtProvider;
import kr.co.sugarmanager.userservice.global.util.StringUtils;
import kr.co.sugarmanager.userservice.group.entity.GroupEntity;
import kr.co.sugarmanager.userservice.group.repository.GroupRepository;
import kr.co.sugarmanager.userservice.user.dto.UserInfoUpdateDTO;
import kr.co.sugarmanager.userservice.user.entity.UserEntity;
import kr.co.sugarmanager.userservice.user.entity.UserImageEntity;
import kr.co.sugarmanager.userservice.user.entity.UserRoleEntity;
import kr.co.sugarmanager.userservice.user.entity.UserSettingEntity;
import kr.co.sugarmanager.userservice.user.repository.UserRepository;
import kr.co.sugarmanager.userservice.user.vo.RoleType;
import kr.co.sugarmanager.userservice.user.vo.SocialType;
import kr.co.sugarmanager.userservice.user.vo.UserInfoValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private ObjectMapper mapper;

    private String accessToken;
    private Map<String, Object> payload;
    private Map<String, Object> header;
    private UserEntity owner;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private List<UserEntity> userList = new ArrayList<>();
    private List<GroupEntity> groupList = new ArrayList<>();
    private Map<String, List<UserEntity>> groupToUser = new HashMap<>();

    private MockHttpServletRequestBuilder getBuilder(String url, HttpMethod method, Map<String, Object> header,
                                                     Object body) throws JsonProcessingException {
        MockHttpServletRequestBuilder result;
        if (method.equals(POST)) {
            result = post(url);
        } else {
            result = get(url);
        }
        if (header != null) {
            header.entrySet().stream()
                    .forEach(entry -> {
                        result.header(entry.getKey(), entry.getValue());
                    });
        }
        if (body != null) {
            result.content(mapper.writeValueAsString(body));
        }
        result.characterEncoding(StandardCharsets.UTF_8);
        return result;
    }

    private void assertError(ResultActions action, ErrorCode errorCode) throws Exception {
        action
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.response", nullValue()))
                .andExpect(jsonPath("$.error.message", is(errorCode.getMessage())))
                .andExpect(jsonPath("$.error.code", is(errorCode.getCode())));
    }

    @BeforeEach
    public void initUsers() {
        for (int i = 0; i < 10; i++) {
            GroupEntity group = GroupEntity.builder()
                    .build();
            groupList.add(group);
            groupToUser.put(group.getGroupCode(), new ArrayList<>());
        }
        groupRepository.saveAll(groupList);
        for (int i = 0; i < 100; i++) {
            String number = Integer.toString(i);
            UserEntity user = UserEntity.builder()
                    .name("user".concat(number))
                    .nickname("nickname".concat(number))
                    .email("email".concat(number).concat("@gmail.com"))
                    .socialId("socialId".concat(number))
                    .socialType(SocialType.KAKAO)
                    .build();
            UserSettingEntity setting = UserSettingEntity.builder()
                    .fcmToken("fcmToken")
                    .build();
            UserImageEntity image = UserImageEntity.builder()
                    .imageUrl("image".concat(number))
                    .build();
            UserRoleEntity role = UserRoleEntity.builder()
                    .role(RoleType.MEMBER)
                    .build();

            user.addSetting(setting);
            user.addProfileImage(image);
            user.addRoles(Set.of(role));
            userList.add(user);
        }
        userRepository.saveAll(userList);
        for (int i = 0; i < userList.size(); i++) {
            UserEntity user = userList.get(i);
            user.joinGroup(groupList.get(i % groupList.size()));
            groupToUser.get(groupList.get(i % groupList.size()).getGroupCode()).add(user);
        }

        //login
        owner = userList.get(0);
        payload = new HashMap<>();
        payload.put("id", owner.getPk());
        payload.put("roles", owner.getRoles().stream()
                .map(r -> r.getRole().getValue())
                .collect(Collectors.toList()));
        accessToken = jwtProvider.createToken(payload);
        header = new HashMap<>();
        header.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        header.put("Authorization", "Bearer ".concat(accessToken));
    }

    @Nested
    @DisplayName("닉네임으로 조회")
    public class FindByNickname {
        private void assertResult(ResultActions actions, UserEntity entity) throws Exception {
            actions
                    .andExpect(jsonPath("$.response.uid", is(entity.getPk()), Long.class))
                    .andExpect(jsonPath("$.response.name", is(entity.getName())))
                    .andExpect(jsonPath("$.response.nickname", is(entity.getNickname())))
                    .andExpect(jsonPath("$.response.email", is(entity.getEmail())))
                    .andExpect(jsonPath("$.response.gender", entity.getGender() == null ? nullValue() :
                            is(entity.getGender() ? "FEMALE" : "MAIL")))
                    .andExpect(jsonPath("$.response.birthday",
                            entity.getBirthday() == null ? nullValue() :
                                    is(formatter.format(entity.getBirthday()))))
                    .andExpect(jsonPath("$.response.height", is(entity.getHeight())))
                    .andExpect(jsonPath("$.response.weight", is(entity.getWeight())))
                    .andExpect(jsonPath("$.response.bloodSugarMin", is(entity.getSugarMin())))
                    .andExpect(jsonPath("$.response.bloodSugarMax", is(entity.getSugarMax())))
                    .andExpect(jsonPath("$.response.profileImage", is(entity.getUserImage().getImageUrl())))
                    .andExpect(jsonPath("$.response.groupCode", entity.getGroup() == null ? nullValue() :
                            is(entity.getGroup().getGroupCode())));
        }

        @Test
        public void 내_정보_조회() throws Exception {
            ResultActions action = mvc.perform(getBuilder("/api/v1/member", HttpMethod.GET, header, null))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.error", nullValue()));
            assertResult(action, owner);
        }

        @Test
        public void 내_그룹_정보_조회() throws Exception {
            UserEntity target = groupToUser.get(owner.getGroup().getGroupCode())
                    .stream().filter(u -> u.getPk() != owner.getPk())
                    .findAny().get();
            ResultActions action = mvc.perform(getBuilder("/api/v1/member/".concat(target.getNickname()), GET, header, null))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.error", nullValue()));
            assertResult(action, target);
        }

        @Test
        public void 다른_그룹_정보_조회() throws Exception {
            UserEntity target = groupToUser.get(
                    groupList.stream().filter(g -> !g.getGroupCode().equals(owner.getGroup().getGroupCode()))
                            .findAny().get().getGroupCode()
            ).get(0);

            ResultActions action = mvc.perform(getBuilder("/api/v1/member/".concat(target.getNickname()), GET, header, null))
                    .andExpect(status().isForbidden());
            assertError(action, ErrorCode.FORBIDDEN_EXCEPTION);
        }
    }

    @Nested
    @DisplayName("업데이트")
    public class Update {
        private UserInfoUpdateDTO.Request req;
        private UserInfoUpdateDTO.Request origin;

        @BeforeEach
        public void init() {
            origin = UserInfoUpdateDTO.Request.builder()
                    .name(owner.getName())
                    .nickname(owner.getNickname())
                    .bloodSugarMax(owner.getSugarMax())
                    .bloodSugarMin(owner.getSugarMin())
                    .birthday(owner.getBirthday())
                    .height(owner.getHeight())
                    .weight(owner.getWeight())
                    .build();
            req = UserInfoUpdateDTO.Request.builder()
                    .name(owner.getName())
                    .nickname(owner.getNickname())
                    .bloodSugarMax(owner.getSugarMax())
                    .bloodSugarMin(owner.getSugarMin())
                    .birthday(owner.getBirthday())
                    .height(owner.getHeight())
                    .weight(owner.getWeight())
                    .build();
        }

        @Nested
        @DisplayName("이름 변경")
        public class Name {
            @Test
            public void 이름_변경_성공() throws Exception {
                req.setName("김현욱");
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.error", nullValue()));

                UserEntity updated = userRepository.findById(owner.getPk()).get();
                assertUpdate(updated, req);
            }

            @Test
            public void 이름_변경_길이_부족_실패() throws Exception {
                req.setName(StringUtils.generateRandomString(UserInfoValidation.NAME.getMin() - 1));
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.NAME_NOT_VALID_EXCEPTION);
                assertUpdateFail(owner);
            }

            @Test
            public void 이름_변경_길이_초과_실패() throws Exception {
                req.setName(StringUtils.generateRandomString(UserInfoValidation.NAME.getMax() + 1));
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.NAME_NOT_VALID_EXCEPTION);
                assertUpdateFail(owner);
            }
        }

        @Nested
        @DisplayName("nickname")
        public class Nickname {
            @Test
            public void 닉네임_변경_성공() throws Exception {
                req.setNickname(StringUtils.generateRandomString(UserInfoValidation.NICKNAME.getMin() + 1));
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk());
            }

            @Test
            public void 닉네임_변경_실패_중복() throws Exception {
                req.setNickname(userList.get(1).getNickname());
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.response", nullValue()))
                        .andExpect(jsonPath("$.error", nullValue()));

                assertUpdateFail(owner);
            }

            @Test
            public void 닉네임_변경_실패_길이_부족() throws Exception {
                req.setNickname(StringUtils.generateRandomString(UserInfoValidation.NICKNAME.getMin() - 1));
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.NICKNAME_NOT_VALID_EXCEPTION);
            }

            @Test
            public void 닉네임_변경_실패_길이_초과() throws Exception {
                req.setNickname(StringUtils.generateRandomString(UserInfoValidation.NICKNAME.getMax() + 1));
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.NICKNAME_NOT_VALID_EXCEPTION);
            }

            @Test
            public void 닉네임_변경_실패_특수문자() throws Exception {
                req.setNickname(StringUtils.generateRandomString(UserInfoValidation.NICKNAME.getMin()).concat("!"));
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.NICKNAME_NOT_VALID_EXCEPTION);
            }
        }

        private void assertUpdate(UserEntity updated, UserInfoUpdateDTO.Request req) {
            assertAll("update",
                    () -> assertThat(updated.getNickname()).isEqualTo(req.getNickname()),
                    () -> assertThat(updated.getName()).isEqualTo(req.getName()),
                    () -> assertThat(updated.getGender()).isEqualTo(req.getGender()),
                    () -> assertThat(updated.getHeight()).isEqualTo(req.getHeight()),
                    () -> assertThat(updated.getWeight()).isEqualTo(req.getWeight()),
                    () -> assertThat(updated.getSugarMax()).isEqualTo(req.getBloodSugarMax()),
                    () -> assertThat(updated.getSugarMin()).isEqualTo(req.getBloodSugarMin())
            );
        }

        private void assertUpdateFail(UserEntity user) {
            assertAll("update",
                    () -> assertThat(user.getNickname()).isEqualTo(origin.getNickname()),
                    () -> assertThat(user.getName()).isEqualTo(origin.getName()),
                    () -> assertThat(user.getGender()).isEqualTo(origin.getGender()),
                    () -> assertThat(user.getHeight()).isEqualTo(origin.getHeight()),
                    () -> assertThat(user.getWeight()).isEqualTo(origin.getWeight()),
                    () -> assertThat(user.getSugarMax()).isEqualTo(origin.getBloodSugarMax()),
                    () -> assertThat(user.getSugarMin()).isEqualTo(origin.getBloodSugarMin())
            );
        }
    }
}
