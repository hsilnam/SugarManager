package kr.co.sugarmanager.alarm.service;

import com.google.firebase.messaging.Message;
import kr.co.sugarmanager.alarm.dto.FCMMessageDTO;

public interface FCMService {
    void send(FCMMessageDTO dto);
}
