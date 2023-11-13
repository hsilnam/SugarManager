package kr.co.sugarmanager.userservice.integ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import kr.co.sugarmanager.userservice.global.util.APIUtils;
import kr.co.sugarmanager.userservice.global.util.JwtProvider;
import kr.co.sugarmanager.userservice.global.util.StringUtils;
import kr.co.sugarmanager.userservice.group.dto.GroupJoinDTO;
import kr.co.sugarmanager.userservice.group.dto.GroupMemberListDTO;
import kr.co.sugarmanager.userservice.group.entity.GroupEntity;
import kr.co.sugarmanager.userservice.group.repository.GroupRepository;
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
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional(isolation = Isolation.READ_COMMITTED)
public class GroupTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private JwtProvider jwtProvider;
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
    @DisplayName("create")
    public class Create {
        @Test
        public void 그룹_생성_성공() throws Exception {
            mvc.perform(getBuilder("/api/v1/group/create", POST, header, null))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.error", nullValue()))
                    .andExpect(jsonPath("$.response.groupCode", is(owner.getGroup().getGroupCode())));
            assertThat(groupRepository.findAll().size()).isEqualTo(1);
        }

        @Test
        public void 그룹_생성_실패_이미_존재() throws Exception {
            GroupEntity group = GroupEntity.builder()
                    .groupCode(StringUtils.generateRandomString(8))
                    .build();
            groupRepository.save(group);
            owner.joinGroup(group);
            mvc.perform(getBuilder("/api/v1/group/create", POST, header, null))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.response.groupCode", nullValue()))
                    .andExpect(jsonPath("$.error", nullValue()));
        }
    }

    @Nested
    @DisplayName("leave")
    class Leave {
        @Test
        public void 그룹_탈퇴_성공_혼자_남은_그룹() throws Exception {
            GroupEntity group = GroupEntity.builder()
                    .groupCode(StringUtils.generateRandomString(8))
                    .build();
            groupRepository.save(group);
            owner.joinGroup(group);
            mvc.perform(getBuilder("/api/v1/group/leave", POST, header, null))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.response", nullValue()))
                    .andExpect(jsonPath("$.error", nullValue()));
            assertThat(groupRepository.findAll().size()).isEqualTo(0);//마지막 남은 사람이 탈퇴하면 그룹또한 삭제
        }

        @Test
        public void 그룹_탈퇴_성공_여러명_남은_그룹() throws Exception {
            GroupEntity group = GroupEntity.builder()
                    .groupCode(StringUtils.generateRandomString(8))
                    .build();
            groupRepository.save(group);
            owner.joinGroup(group);
            for (int i = 0; i < 10; i++) {
                userList.get(i).joinGroup(group);
            }
            mvc.perform(getBuilder("/api/v1/group/leave", POST, header, null))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.response", nullValue()))
                    .andExpect(jsonPath("$.error", nullValue()));
            assertThat(groupRepository.findAll().size()).isEqualTo(1);//남아있어야함
        }

        @Test
        public void 그룹_탈퇴_실패_가입한_그룹_없음() throws Exception {
            ResultActions action = mvc.perform(getBuilder("/api/v1/group/leave", POST, header, null))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.response", nullValue()));
            assertError(action, ErrorCode.GROUP_NOT_JOIN_EXCEPTION);
        }
    }

    @Nested
    @DisplayName("join")
    class Join {
        GroupEntity group = GroupEntity.builder()
                .groupCode(StringUtils.generateRandomString(10))
                .build();
        GroupJoinDTO.Request req = GroupJoinDTO.Request.builder()
                .groupCode(group.getGroupCode())
                .build();

        @Test
        public void 그룹_가입_성공() throws Exception {
            groupRepository.save(group);
            mvc.perform(getBuilder("/api/v1/group/join", POST, header, req))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.error", nullValue()))
                    .andExpect(jsonPath("$.response", nullValue()));
            assertThat(groupRepository.getGroupMember(group.getGroupCode())).isEqualTo(1);
        }

        @Test
        public void 그룹_가입_실패_이미_가입한_그룹_존재() throws Exception {
            groupRepository.save(group);
            owner.joinGroup(group);
            mvc.perform(getBuilder("/api/v1/group/join", POST, header, req))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(false)))
                    .andExpect(jsonPath("$.error", nullValue()))
                    .andExpect(jsonPath("$.response", nullValue()));
        }

        @Test
        public void 그룹_가입_실패_존재하지_않는_그룹() throws Exception {
            req.setGroupCode(StringUtils.generateRandomString(10));//다른 랜덤한 그룹 코드로 설정
            ResultActions action = mvc.perform(getBuilder("/api/v1/group/join", POST, header, req))
                    .andExpect(status().isNotFound());
            assertError(action, ErrorCode.GROUP_NOT_FOUND_EXCEPTION);
        }
    }

    @Nested
    @DisplayName("getGroupMember")
    public class GetGroupMember {
        @Test
        public void 그룹원_조회_성공_가입된_그룹이_없는경우() throws Exception {
            mvc.perform(getBuilder("/api/v1/group", GET, header, null))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.error", nullValue()))
                    .andExpect(jsonPath("$.response.users").isEmpty());
        }

        @Test
        public void 그룹원_조회_성공_가입된_그룹이_있는경우() throws Exception {
            GroupEntity group = GroupEntity.builder()
                    .groupCode(StringUtils.generateRandomString(10))
                    .build();
            groupRepository.save(group);
            List<UserEntity> sameGroup = new ArrayList<>();
            sameGroup.add(owner);
            //그룹에 가입시킴
            owner.joinGroup(group);
            for (int i = 0; i < 10; i++) {
                if (userList.get(i).getPk() != owner.getPk()) {
                    userList.get(i).joinGroup(group);
                    sameGroup.add(userList.get(i));
                }
            }

            ResultActions action = mvc.perform(getBuilder("/api/v1/group", GET, header, null))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.error", nullValue()));

            String content = action.andReturn().getResponse().getContentAsString();
            APIUtils.ApiResult result = mapper.readValue(content, APIUtils.ApiResult.class);
            GroupMemberListDTO.Response response = mapper.convertValue(result.getResponse(), GroupMemberListDTO.Response.class);
            List<GroupMemberListDTO.UserInfo> responseUsers = response.getUsers();
            List<GroupMemberListDTO.UserInfo> expected = sameGroup.stream()
                    .map(u -> GroupMemberListDTO.UserInfo.builder()
                            .uid(u.getPk())
                            .nickname(u.getNickname())
                            .profileUrl(u.getUserImage().getImageUrl())
                            .build())
                    .toList();
            assertThat(responseUsers.size()).isEqualTo(expected.size());
            for (int i = 0; i < responseUsers.size(); i++) {
                GroupMemberListDTO.UserInfo actual = responseUsers.get(i);
                assertThat(actual).isNotIn(expected);
            }
        }
    }
}
