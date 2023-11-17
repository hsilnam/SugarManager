package kr.co.sugarmanager.business.menu.exception;

import kr.co.sugarmanager.business.global.exception.CustomException;
import kr.co.sugarmanager.business.global.exception.ErrorCode;

public class MenuException extends CustomException {
    public MenuException(ErrorCode errorCode) {
        super(errorCode);
    }
}