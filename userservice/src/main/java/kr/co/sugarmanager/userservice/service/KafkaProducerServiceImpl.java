package kr.co.sugarmanager.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.userservice.dto.MessageDTO;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements ProducerService {
    @Value("${kafka.properties.topic.push-notification}")
    private String pushNotificationTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper;

    @Override
    @Async
    public void sendMessage(MessageDTO dto) {
        try {
            kafkaTemplate.send(pushNotificationTopic, mapper.writeValueAsString(dto));
            kafkaTemplate.flush();
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION);
        }
    }
}
