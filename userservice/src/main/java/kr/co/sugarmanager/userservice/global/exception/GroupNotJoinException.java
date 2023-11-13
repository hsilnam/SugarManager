package kr.co.sugarmanager.userservice.global.exception;

public class GroupNotJoinException extends CustomException {
    public GroupNotJoinException(ErrorCode errorCode) {
        super(errorCode);
    }
}
