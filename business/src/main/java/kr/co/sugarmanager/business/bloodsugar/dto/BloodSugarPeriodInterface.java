package kr.co.sugarmanager.business.bloodsugar.dto;

public interface BloodSugarPeriodInterface {
    Double getBloodSugarBefore();
    Double getBloodSugarAfter();
    SUGARBLOODSTATUS getBloodSugarBeforeStatus();
    SUGARBLOODSTATUS getBloodSugarAfterStatus();
    String getTime();
    int getCount();
}