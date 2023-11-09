package kr.co.sugarmanager.business.challenge.service;

import kr.co.sugarmanager.business.challenge.dto.*;

public interface ChallengeService {
    TodayChallengesDTO.Response todaysChallenges();
    ChallengeAddDTO.Response addChallenge(Long userPk, ChallengeAddDTO.Request dto);
    ChallengeDeleteDTO.Response deleteChallenge(Long userPk, ChallengeDeleteDTO.Request dto);
    UserChallengeAllDTO.Response userChallengesAll(Long userPk);
    ChallengePokeDTO.Response infoForPoke(Long userPk, ChallengePokeDTO.Request dto);
}
