package kr.co.sugarmanager.business.challenge.service;

import kr.co.sugarmanager.business.challenge.dto.*;

public interface ChallengeService {
    TodayChallengesDTO.Response todaysChallenges();
    ChallengeAddDTO.Response addChallenge(Long pk, ChallengeAddDTO.Request dto);
    ChallengeDeleteDTO.Response deleteChallenge(Long pk, ChallengeDeleteDTO.Request dto);
    UserChallengeAllDTO.Response userChallengesAll(Long pk, String nickname);
    UserChallengeInfoDTO.Response userChallengeInfo(Long pk, String nickname, Long challengePk);
    ChallengeClaimDTO.Response claim(Long pk, Long challengePk);
}
