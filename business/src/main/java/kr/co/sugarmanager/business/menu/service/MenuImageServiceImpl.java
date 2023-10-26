package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.menu.dto.ImageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuImageServiceImpl implements MenuImageService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public int produceMessage(Long pk, String type, List<MultipartFile> multipartFile) {
        AtomicInteger sendImageCount = new AtomicInteger(0);
        try {
            for (MultipartFile file : multipartFile) {
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                checkImageFileExtension(extension);

                ImageDTO imageDTO = ImageDTO.builder()
                        .pk(pk)
                        .type(type)
                        .file(file.getBytes())
                        .build();

                CompletableFuture completableFuture = kafkaTemplate.send("image", imageDTO);

                completableFuture.whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("ERROR Kafka error happened", ex);
                    } else {
                        sendImageCount.incrementAndGet();
                        log.info("SUCCESS!! This is the result: {}", result);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sendImageCount.get();
    }

    private void checkImageFileExtension(String extension) {
        if (!extension.toLowerCase().matches("jpg|jpeg|png")) {
            throw new ValidationException(ErrorCode.INVALID_IMAGE_TYPE_ERROR);
        }
    }
}
