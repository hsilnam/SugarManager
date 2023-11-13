package kr.co.sugarmanager.business.bloodsugar.dto;

public interface BloodSugarPeriodInterface {
    Double getBloodSugarBefore();
    Double getBloodSugarAfter();
    String getTime();
    int getCount();
}