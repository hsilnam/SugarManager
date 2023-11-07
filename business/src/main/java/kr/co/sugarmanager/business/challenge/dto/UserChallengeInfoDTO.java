package kr.co.sugarmanager.business.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeInfoDTO {
    private String challengeTitle;
    private int goal;
    private String type;
    private boolean alert;
    private int hour;
    private int minute;
    private List<String> days;


}