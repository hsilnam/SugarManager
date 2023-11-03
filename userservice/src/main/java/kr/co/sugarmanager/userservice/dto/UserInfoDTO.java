package kr.co.sugarmanager.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

public class UserInfoDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private long userPk;
        private String targetNickname;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private long uid;
        private String name;
        private String nickname;
        private String email;
        private String gender;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date birthday;
        private Integer height;
        private Integer weight;
        @JsonProperty("blood_sugar_min")
        private Integer bloodSugarMin;
        @JsonProperty("blood_sugar_max")
        private Integer bloodSugarMax;
        @JsonProperty("profile_image")
        private String profileImage;
        @JsonProperty("group_code")
        private String groupCode;
    }
}