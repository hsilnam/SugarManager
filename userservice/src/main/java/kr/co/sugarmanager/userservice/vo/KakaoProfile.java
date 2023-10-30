package kr.co.sugarmanager.userservice.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoProfile {
    private long id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;

    @Getter
    @Builder
    public static class Properties {
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }

    @Getter
    @Builder
    public static class KakaoAccount {
        private Boolean profile_nickname_needs_agreement;
        private Boolean profile_image_needs_agreement;
        private Profile profile;
        private Boolean name_nees_agreement;
        private String name;
        private Boolean email_needs_agreement;
        private Boolean is_email_valid;
        private Boolean is_email_verified;
        private String email;
        private Boolean age_range_needs_agreement;
        private String age_range;
        private Boolean birthyear_needs_agreement;
        private String birthyear;//YYYY
        private Boolean birthday_needs_agreement;
        private String birthday;//MMDD
        private String birthday_type;
        private Boolean gender_needs_agreement;
        private String gender;//male / female
        private Boolean phone_number_needs_agreement;
        private String phone_number;

        @Getter
        @Builder
        public static class Profile {
            private String nickname;
            private String thumbnail_image_url;
            private String profile_image_url;
            private Boolean is_default_image;
        }
    }
}