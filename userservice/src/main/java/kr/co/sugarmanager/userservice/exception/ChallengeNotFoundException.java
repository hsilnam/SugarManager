package kr.co.sugarmanager.userservice.exception;

public class ChallengeNotFoundException extends CustomException {
    public ChallengeNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
