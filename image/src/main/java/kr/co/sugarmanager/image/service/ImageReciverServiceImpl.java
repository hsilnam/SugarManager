package kr.co.sugarmanager.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageReciverServiceImpl implements ImageReciverService {

    private final ImageService imageSaveService;

    @KafkaListener(topics = "image")
    public void getMessage(String message) {
        log.info("message: {}", message);
        imageSaveService.service(message);
    }
}
