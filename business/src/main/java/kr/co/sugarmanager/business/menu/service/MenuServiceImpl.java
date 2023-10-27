package kr.co.sugarmanager.business.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.menu.dto.ImageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void produceMessage(Long pk, String type, List<MultipartFile> multipartFile) {
        try {
            for (MultipartFile file : multipartFile) {
                ImageDTO imageDTO = createImageDTO(pk, type, file);
                kafkaTemplate.send("image", objectMapper.writeValueAsString(imageDTO));
            }
        } catch (JsonProcessingException e) {
            log.info("Json fail: {}", e);
        }
    }

    public ImageDTO createImageDTO(Long pk, String type, MultipartFile multipartFile) {
        String originalFilename = multipartFile.getName();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        checkImageFileExtension(extension);
        try {
            return ImageDTO.builder()
                    .pk(pk)
                    .type(type)
                    .extension(extension)
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .file(multipartFile.getBytes())
                    .build();
        } catch (IOException e) {
            throw new ValidationException(ErrorCode.INVALID_IMAGE_TYPE_ERROR);
        }
    }

    private void checkImageFileExtension(String extension) {
        if (!extension.toLowerCase().matches("jpg|jpeg|png")) {
            throw new ValidationException(ErrorCode.INVALID_IMAGE_TYPE_ERROR);
        }
    }
}
