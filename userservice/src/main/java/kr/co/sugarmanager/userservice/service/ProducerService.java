package kr.co.sugarmanager.userservice.service;

import kr.co.sugarmanager.userservice.dto.MessageDTO;

public interface ProducerService {
    void sendMessage(MessageDTO dto);
}
