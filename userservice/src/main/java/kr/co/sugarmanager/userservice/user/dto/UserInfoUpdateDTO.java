package kr.co.sugarmanager.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

public class UserInfoUpdateDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonIgnore
        private long userPk;
        @JsonProperty("name")
        private String name;
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("birthday")
        private Date birthday;
        @JsonProperty("height")
        private Integer height;
        @JsonProperty("weight")
        private Integer weight;
        @JsonProperty("blood_sugar_min")
        private Integer bloodSugarMin;
        @JsonProperty("blood_sugar_max")
        private Integer bloodSugarMax;
        @JsonProperty("gender")
        private String gender;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonIgnore
        private boolean success;
    }
}
