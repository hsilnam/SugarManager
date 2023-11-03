package kr.co.sugarmanager.business.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.menu.dto.ImageDTO;
import kr.co.sugarmanager.business.menu.dto.ImageTypeEnum;
import kr.co.sugarmanager.business.menu.dto.OperationTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuImageServiceImpl implements MenuImageService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void saveImage(Long pk, ImageTypeEnum imageTypeEnum, List<MultipartFile> multipartFile) {
        if (multipartFile == null) return;
        try {
            for (MultipartFile file : multipartFile) {
                ImageDTO imageDTO = createImageDTO(pk, imageTypeEnum, file);
                kafkaTemplate.send("image", createSaveMessage(imageDTO));
                kafkaTemplate.flush();
            }
        } catch (JsonProcessingException e) {
            log.info("Json fail: {}", e);
        }
    }

    public void deleteImage(List<Long> pkList) {

    }

    public String createSaveMessage(ImageDTO imageDTO) throws JsonProcessingException {
        ObjectNode message = objectMapper.createObjectNode();
        message.put("operationTypeEnum", OperationTypeEnum.SAVE.name());
        message.put("imageInfo", imageDTO.getObjectNode(objectMapper));
        return objectMapper.writeValueAsString(message);
    }

    public ImageDTO createImageDTO(Long pk, ImageTypeEnum imageTypeEnum, MultipartFile multipartFile) {
        String originalFilename = multipartFile.getName();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        checkImageFileExtension(extension);
        try {
            return ImageDTO.builder()
                    .pk(String.valueOf(pk))
                    .imageTypeEnum(imageTypeEnum)
                    .extension(extension)
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .file(multipartFile.getBytes())
                    .build();
        } catch (IOException e) {
            throw new ValidationException(ErrorCode.INVALID_IMAGE_TYPE_ERROR);
        }
    }

    public void checkImageFileExtension(String extension) {
        if (!extension.toLowerCase().matches("jpg|jpeg|png")) {
            throw new ValidationException(ErrorCode.INVALID_IMAGE_TYPE_ERROR);
        }
    }
}
