package kr.co.sugarmanager.alarm.service;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import kr.co.sugarmanager.alarm.dto.FCMMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMServiceImpl implements FCMService {

    @Override
    public void send(FCMMessageDTO dto){

        Notification notification = Notification.builder()
                .setTitle(dto.getTitle())
                .setBody(dto.getBody())
                .build();
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(dto.getFcmToken())
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseException e){
            e.printStackTrace();
        };
    }

}
