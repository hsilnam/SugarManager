package kr.co.sugarmanager.business.bloodsugar.exception;

import kr.co.sugarmanager.business.global.exception.CustomException;
import kr.co.sugarmanager.business.global.exception.ErrorCode;

public class BloodSugarException extends CustomException {
    public BloodSugarException(ErrorCode errorCode) {
        super(errorCode);
    }
}
