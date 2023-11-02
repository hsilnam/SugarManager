package kr.co.sugarmanager.image.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.image.dto.OperationTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageReciverServiceImpl implements ImageReciverService {

    private final ImageService imageService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "image")
    public void getMessage(String message) {
        log.info("message: {}", message);
        try {
            Map<String, Object> map = objectMapper.readValue(message, Map.class);
            String operationType = (String) map.get("operationTypeEnum");
            Map<String, Object> imageInfoMap = (Map<String, Object>) map.get("imageInfo");
            imageService.service(OperationTypeEnum.valueOf(operationType), imageInfoMap);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: {}", e);
        } catch (KafkaException e) {
            log.error("KafkaException: {}", e);
        }
    }
}
