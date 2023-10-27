package kr.co.sugarmanager.business.menu.service;

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

    public void produceMessage(Long pk, String type, List<MultipartFile> multipartFile) {
        for (MultipartFile file : multipartFile) {
            ImageDTO imageDTO = createImageDTO(pk, type, file);
            kafkaTemplate.send("image", imageDTO);
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
