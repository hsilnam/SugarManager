package kr.co.sugarmanager.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
        @JsonIgnore
        private boolean success;
        private long uid;
        private String name;
        private String nickname;
        private String email;
        private String gender;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date birthday;
        private Integer height;
        private Integer weight;
        private Integer bloodSugarMin;
        private Integer bloodSugarMax;
        private String profileImage;
        private String groupCode;
    }
}