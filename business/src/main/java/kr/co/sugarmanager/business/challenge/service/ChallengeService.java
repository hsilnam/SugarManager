package kr.co.sugarmanager.business.challenge.service;

import kr.co.sugarmanager.business.challenge.dto.*;

public interface ChallengeService {
    TodayChallengesDTO.Response todaysChallenges();
    ChallengeAddDTO.Response addChallenge(ChallengeAddDTO.Request dto);
    ChallengeDeleteDTO.Response deleteChallenge(ChallengeDeleteDTO.Request dto);
    UserChallengeAllDTO.Response userChallengesAll(String nickname);
    UserChallengeInfoDTO.Response userChallengeInfo(Long userPk, Long challengePk);
    ChallengePokeDTO.Response infoForPoke(ChallengePokeDTO.Request dto);
}
