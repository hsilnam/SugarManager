package kr.co.sugarmanager.userservice.exception;

public class GroupNotFoundException extends CustomException {
    public GroupNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
