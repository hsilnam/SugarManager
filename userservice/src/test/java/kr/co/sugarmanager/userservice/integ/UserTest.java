package kr.co.sugarmanager.userservice.integ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import kr.co.sugarmanager.userservice.challenge.entity.ChallengeEntity;
import kr.co.sugarmanager.userservice.challenge.repository.ChallengeRepository;
import kr.co.sugarmanager.userservice.global.dto.MessageDTO;
import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import kr.co.sugarmanager.userservice.global.service.ProducerService;
import kr.co.sugarmanager.userservice.global.util.APIUtils;
import kr.co.sugarmanager.userservice.global.util.JwtProvider;
import kr.co.sugarmanager.userservice.global.util.StringUtils;
import kr.co.sugarmanager.userservice.group.entity.GroupEntity;
import kr.co.sugarmanager.userservice.group.repository.GroupRepository;
import kr.co.sugarmanager.userservice.user.dto.AlarmDTO;
import kr.co.sugarmanager.userservice.user.dto.AlarmUpdateDTO;
import kr.co.sugarmanager.userservice.user.dto.PokeDTO;
import kr.co.sugarmanager.userservice.user.dto.UserInfoUpdateDTO;
import kr.co.sugarmanager.userservice.user.entity.UserEntity;
import kr.co.sugarmanager.userservice.user.entity.UserImageEntity;
import kr.co.sugarmanager.userservice.user.entity.UserRoleEntity;
import kr.co.sugarmanager.userservice.user.entity.UserSettingEntity;
import kr.co.sugarmanager.userservice.user.repository.UserRepository;
import kr.co.sugarmanager.userservice.user.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.lenient;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @Autowired
    private ChallengeRepository challengeRepository;
    @MockBean
    private ProducerService producerService;

    private String accessToken;
    private Map<String, Object> payload;
    private Map<String, Object> header;
    private UserEntity owner;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private List<UserEntity> userList = new ArrayList<>();
    private List<GroupEntity> groupList = new ArrayList<>();
    private Map<String, List<UserEntity>> groupToUser = new HashMap<>();
    private List<MessageDTO> messageList = new ArrayList<>();

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
    public void producerServiceMocking() {
        lenient().doAnswer(invocation -> {
            MessageDTO message = invocation.getArgument(0, MessageDTO.class);
            messageList.add(message);
            return null;
        }).when(producerService).sendMessage(ArgumentMatchers.any(MessageDTO.class));
    }

    @BeforeEach
    public void initUsers() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            GroupEntity group = GroupEntity.builder()
                    .groupCode(StringUtils.generateRandomString(8))
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
                    .sugarMin(UserInfoValidation.BLOODSUGARMIN.getMin() + 1)
                    .sugarMax(UserInfoValidation.BLOODSUGARMAX.getMax() - 1)
                    .socialType(SocialType.KAKAO)
                    .build();
            UserSettingEntity setting = UserSettingEntity.builder()
                    .fcmToken("fcmToken")
                    .challengeAlert(random.nextBoolean())
                    .pokeAlert(random.nextBoolean())
                    .sugarAlert(random.nextBoolean())
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
            UserEntity target = userList.stream().filter(u -> u.getPk() != owner.getPk())
                    .findAny().get();
            GroupEntity other = GroupEntity.builder()
                    .groupCode(StringUtils.generateRandomString(10))
                    .build();
            groupRepository.save(other);
            target.joinGroup(other);

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

                assertUpdate(owner, req);
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
                assertUpdate(owner, req);
            }

            @Test
            public void 닉네임_변경_성공_한글() throws Exception {
                req.setNickname("김현욱이올시다");
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk());
                assertUpdate(owner, req);
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

        @Nested
        @DisplayName("Height")
        public class Height {
            @Test
            public void 키_수정_성공() throws Exception {
                req.setHeight(UserInfoValidation.HEIGHT.getMin() + 1);
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.response", nullValue()))
                        .andExpect(jsonPath("$.error", nullValue()));
                assertUpdate(owner, req);
            }

            @Test
            public void 키_수정_실패_최소() throws Exception {
                req.setHeight(UserInfoValidation.HEIGHT.getMin() - 1);
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.HEIGHT_NOT_VALID_EXCEPTION);
            }

            @Test
            public void 키_수정_실패_최대() throws Exception {
                req.setHeight(UserInfoValidation.HEIGHT.getMax() + 1);
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.HEIGHT_NOT_VALID_EXCEPTION);
            }
        }

        @Nested
        @DisplayName("weight")
        public class Weight {
            @Test
            public void 체중_수정_성공() throws Exception {
                req.setWeight(UserInfoValidation.WEIGHT.getMin() + 1);
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.response", nullValue()))
                        .andExpect(jsonPath("$.error", nullValue()));
                assertUpdate(owner, req);
            }

            @Test
            public void 체중_수정_실패_최소() throws Exception {
                req.setWeight(UserInfoValidation.WEIGHT.getMin() - 1);
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.WEIGHT_NOT_VALID_EXCEPTION);
            }

            @Test
            public void 체중_수정_실패_최대() throws Exception {
                req.setWeight(UserInfoValidation.WEIGHT.getMax() + 1);
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.WEIGHT_NOT_VALID_EXCEPTION);
            }
        }

        @Nested
        @DisplayName("blood sugar min")
        public class BloodSugarMin {
            @Test
            public void 혈당_최저_수정_성공() throws Exception {
                req.setBloodSugarMin(UserInfoValidation.BLOODSUGARMIN.getMin() + 1);
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.response", nullValue()))
                        .andExpect(jsonPath("$.error", nullValue()));
                assertUpdate(owner, req);
            }

            @Test
            public void 혈당_최저_수정_실패_최소() throws Exception {
                req.setBloodSugarMin(UserInfoValidation.BLOODSUGARMIN.getMin() - 1);
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.BLOODSUGARMIN_NOT_VALID_EXCEPTION);
            }

            @Test
            public void 혈당_최저_수정_실패_최대() throws Exception {
                req.setBloodSugarMin(UserInfoValidation.BLOODSUGARMIN.getMax() + 1);
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.BLOODSUGARMIN_NOT_VALID_EXCEPTION);
            }
        }

        @Nested
        @DisplayName("blood sugar max")
        public class BloodSugarMax {
            @Test
            public void 혈당_최고_수정_성공() throws Exception {
                req.setBloodSugarMax(UserInfoValidation.BLOODSUGARMAX.getMin() + 1);
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.response", nullValue()))
                        .andExpect(jsonPath("$.error", nullValue()));
                assertUpdate(owner, req);
            }

            @Test
            public void 혈당_최고_수정_실패_최소() throws Exception {
                req.setBloodSugarMax(UserInfoValidation.BLOODSUGARMAX.getMin() - 1);
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.BLOODSUGARMAX_NOT_VALID_EXCEPTION);
            }

            @Test
            public void 혈당_최고_수정_실패_최대() throws Exception {
                req.setBloodSugarMax(UserInfoValidation.BLOODSUGARMAX.getMax() + 1);
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.BLOODSUGARMAX_NOT_VALID_EXCEPTION);
            }
        }

        @Nested
        @DisplayName("gender")
        public class Gender {
            @Test
            public void 성별_설정_성공_Null() throws Exception {
                req.setGender(null);
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.response", nullValue()))
                        .andExpect(jsonPath("$.error", nullValue()));
                assertUpdate(owner, req);
            }

            @Test
            public void 성별_설정_성공_MALE() throws Exception {
                req.setGender("MALE");
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.response", nullValue()))
                        .andExpect(jsonPath("$.error", nullValue()));
                assertUpdate(owner, req);
            }

            @Test
            public void 성별_설정_성공_FEMALE() throws Exception {
                req.setGender("FEMALE");
                mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.response", nullValue()))
                        .andExpect(jsonPath("$.error", nullValue()));
                assertUpdate(owner, req);
            }

            @Test
            public void 성별_설정_실패_소문자() throws Exception {
                req.setGender("female");
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.GENDER_NOT_VALID_EXCEPTION);
            }

            @Test
            public void 성별_설정_실패_오타() throws Exception {
                req.setGender("femail");
                ResultActions action = mvc.perform(getBuilder("/api/v1/member/edit", POST, header, req))
                        .andExpect(status().isBadRequest());
                assertError(action, ErrorCode.GENDER_NOT_VALID_EXCEPTION);
            }
        }

        private void assertUpdate(UserEntity updated, UserInfoUpdateDTO.Request req) {
            assertAll("update",
                    () -> assertThat(updated.getNickname()).isEqualTo(req.getNickname()),
                    () -> assertThat(updated.getName()).isEqualTo(req.getName()),
                    () -> assertThat(updated.getGender()).isEqualTo(req.getGender() == null ? null : req.getGender().equals("MALE") ? false : true),
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

    @Nested
    @DisplayName("alarm")
    public class Alarm {
        void assertAlarmStatus(ResultActions action, UserEntity user) throws UnsupportedEncodingException, JsonProcessingException {
            String content = action.andReturn().getResponse().getContentAsString();
            APIUtils.ApiResult apiResult = mapper.readValue(content, APIUtils.ApiResult.class);
            AlarmDTO.Response response = mapper.convertValue(apiResult.getResponse(), AlarmDTO.Response.class);
            response.getAlarms().stream().forEach(a -> {
                AlarmDTO.AlarmInfo owners = owner.getSetting().getAlarmInfos().stream().filter(info -> info.getCategory().equals(a.getCategory())).findAny().get();
                assertThat(a.isStatus()).isEqualTo(owners.isStatus());
            });
        }

        @Test
        public void 알람_조회_성공() throws Exception {
            ResultActions action = mvc.perform(getBuilder("/api/v1/member/alarm", GET, header, null))
                    .andDo(print())
                    .andExpect(status().isOk());
            assertAlarmStatus(action, owner);
        }
    }

    @Nested
    @DisplayName("alarm save")
    public class AlarmSave {
        AlarmUpdateDTO.Request req;

        @Test
        public void 알람_수정_성공() throws Exception {
            for (AlertType category : AlertType.values()) {
                //해당 카테고리의 상태에 반대되는 상태로 변경
                req = AlarmUpdateDTO.Request.builder()
                        .category(category)
                        .status(
                                !owner.getSetting().getAlarmInfos()
                                        .stream()
                                        .filter(a -> a.getCategory().equals(category))
                                        .findAny().get().isStatus()
                        )
                        .build();
                mvc.perform(getBuilder("/api/v1/member/alarm/save", POST, header, req))
                        .andExpect(status().isOk());
            }
        }
    }

    @Nested
    @DisplayName("poke")
    public class Poke {
        @Test
        public void 찌르기_성공_챌린지() throws Exception {
            //같은 그룹 사람
            UserEntity target = userList.stream()
                    .filter(u -> u.getPk() != owner.getPk())
                    .findAny().get();
            target.joinGroup(owner.getGroup());

            Field field = UserSettingEntity.class.getDeclaredField("pokeAlert");
            field.setAccessible(true);
            field.set(target.getSetting(), true);
            field.setAccessible(false);

            ChallengeEntity targetChallenge = ChallengeEntity.builder()
                    .title("target title")
                    .goal("0")
                    .alert(true)
                    .build();

            challengeRepository.save(targetChallenge);

            PokeDTO.Request req = PokeDTO.Request.builder()
                    .nickname(target.getNickname())
                    .type(PokeType.CHALLENGE)
                    .challengeId(targetChallenge.getPk())
                    .build();

            mvc.perform(getBuilder("/api/v1/member/poke", POST, header, req))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.error", nullValue()))
                    .andExpect(jsonPath("$.response.nickname", is(req.getNickname())))
                    .andExpect(jsonPath("$.response.type", is(req.getType().name())))
                    .andExpect(jsonPath("$.response.challengeId", is(req.getChallengeId()), Long.class));
            assertThat(messageList.size()).isEqualTo(1);
            MessageDTO messageDTO = messageList.get(0);
            assertThat(messageDTO.getBody()).isEqualTo(targetChallenge.getTitle());
            assertThat(messageDTO.getTitle()).isEqualTo(owner.getNickname().concat(" 님의 찌르기입니다."));
            assertThat(messageDTO.getFcmToken()).isEqualTo(target.getSetting().getFcmToken());
        }

        @Test
        public void 찌르기_성공_혈당() throws Exception {
            //같은 그룹 사람
            UserEntity target = userList.stream()
                    .filter(u -> u.getPk() != owner.getPk())
                    .findAny().get();
            target.joinGroup(owner.getGroup());

            Field field = UserSettingEntity.class.getDeclaredField("pokeAlert");
            field.setAccessible(true);
            field.set(target.getSetting(), true);
            field.setAccessible(false);

            PokeDTO.Request req = PokeDTO.Request.builder()
                    .nickname(target.getNickname())
                    .type(PokeType.BLOODSUGAR)
                    .build();

            mvc.perform(getBuilder("/api/v1/member/poke", POST, header, req))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.error", nullValue()))
                    .andExpect(jsonPath("$.response.nickname", is(req.getNickname())))
                    .andExpect(jsonPath("$.response.type", is(req.getType().name())))
                    .andExpect(jsonPath("$.response.challengeId", is(req.getChallengeId())));
            assertThat(messageList.size()).isEqualTo(1);
            MessageDTO messageDTO = messageList.get(0);
            assertThat(messageDTO.getBody()).isEqualTo("혈당을 체크하세요!");
            assertThat(messageDTO.getTitle()).isEqualTo(owner.getNickname().concat(" 님의 찌르기입니다."));
            assertThat(messageDTO.getFcmToken()).isEqualTo(target.getSetting().getFcmToken());
        }

        @Test
        public void 찌르기_실패_상대방_알림_off() throws Exception {
            //같은 그룹 사람
            UserEntity target = userList.stream()
                    .filter(u -> u.getPk() != owner.getPk())
                    .findAny().get();
            target.joinGroup(owner.getGroup());

            Field field = UserSettingEntity.class.getDeclaredField("pokeAlert");
            field.setAccessible(true);
            field.set(target.getSetting(), false);
            field.setAccessible(false);

            PokeDTO.Request req = PokeDTO.Request.builder()
                    .nickname(target.getNickname())
                    .type(PokeType.BLOODSUGAR)
                    .build();

            mvc.perform(getBuilder("/api/v1/member/poke", POST, header, req))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.error", nullValue()))
                    .andExpect(jsonPath("$.response.nickname", nullValue()))
                    .andExpect(jsonPath("$.response.type", nullValue()))
                    .andExpect(jsonPath("$.response.challengeId", nullValue()));
            assertThat(messageList.size()).isEqualTo(0);
        }

        @Test
        public void 찌르기_실패_다른_그룹() throws Exception {
            //다른 그룹 사람
            UserEntity target = userList.stream().filter(u -> u.getPk() != owner.getPk())
                    .findAny().get();
            GroupEntity other = GroupEntity.builder()
                    .groupCode(StringUtils.generateRandomString(10))
                    .build();
            groupRepository.save(other);
            target.joinGroup(other);

            Field field = UserSettingEntity.class.getDeclaredField("pokeAlert");
            field.setAccessible(true);
            field.set(target.getSetting(), true);
            field.setAccessible(false);

            PokeDTO.Request req = PokeDTO.Request.builder()
                    .nickname(target.getNickname())
                    .type(PokeType.BLOODSUGAR)
                    .build();

            ResultActions action = mvc.perform(getBuilder("/api/v1/member/poke", POST, header, req))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.response", nullValue()));
            assertThat(messageList.size()).isEqualTo(0);
            assertError(action, ErrorCode.FORBIDDEN_EXCEPTION);
        }
    }
}
