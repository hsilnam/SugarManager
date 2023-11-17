package kr.co.sugarmanager.userservice.global.service;

import kr.co.sugarmanager.userservice.global.dto.MessageDTO;

public interface ProducerService {
    void sendMessage(MessageDTO dto);
}
