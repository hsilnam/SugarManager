package kr.co.sugarmanager.business.challenge.dto;

import java.time.LocalDateTime;

public interface LogsAndLatestInterface {
    Integer getCount();
    LocalDateTime getLatest();
}
