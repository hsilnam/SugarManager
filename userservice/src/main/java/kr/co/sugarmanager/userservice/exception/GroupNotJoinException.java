package kr.co.sugarmanager.userservice.exception;

public class GroupNotJoinException extends CustomException {
    public GroupNotJoinException(ErrorCode errorCode) {
        super(errorCode);
    }
}
