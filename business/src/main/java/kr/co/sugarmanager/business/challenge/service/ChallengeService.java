package kr.co.sugarmanager.business.challenge.service;

import kr.co.sugarmanager.business.challenge.dto.ChallengeAddDTO;
import kr.co.sugarmanager.business.challenge.dto.ChallengeDeleteDTO;
import kr.co.sugarmanager.business.challenge.dto.TodayChallengesDTO;

public interface ChallengeService {
    TodayChallengesDTO.Response todaysChallenges();
    ChallengeAddDTO.Response addChallenge(Long userPk, ChallengeAddDTO.Request dto);
    ChallengeDeleteDTO.Response deleteChallenge(Long userPk, ChallengeDeleteDTO.Request dto);
}
