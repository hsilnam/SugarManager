package kr.co.sugarmanager.userservice.global.exception;

public class ChallengeNotFoundException extends CustomException {
    public ChallengeNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
