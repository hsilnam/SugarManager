package kr.co.sugarmanager.userservice.global.exception;

public class GroupNotFoundException extends CustomException {
    public GroupNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
