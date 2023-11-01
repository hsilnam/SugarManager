package kr.co.sugarmanager.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.image.dto.ImageDTO;
import kr.co.sugarmanager.image.dto.OperationTypeEnum;
import kr.co.sugarmanager.image.entity.FAQImageEntity;
import kr.co.sugarmanager.image.entity.FoodImageEntity;
import kr.co.sugarmanager.image.entity.ImageEntity;
import kr.co.sugarmanager.image.repository.FAQImageRepository;
import kr.co.sugarmanager.image.repository.FoodImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final AmazonS3 amazonS3;
    private final FoodImageRepository foodImageRepository;
    private final FAQImageRepository faqImageRepository;
    private final ObjectMapper objectMapper;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public void service(String message) {
        log.info("message: {}", message);
        try {
            Map<String, Object> map = objectMapper.readValue(message, Map.class);

            String operationType = (String) map.get("operationTypeEnum");
            if (operationType.equals(OperationTypeEnum.SAVE.name())) {
                Map<String, Object> imageInfoMap = (Map<String, Object>) map.get("imageInfo");
                ImageDTO imageDTO = new ImageDTO(imageInfoMap);
                save(imageDTO);
            } else if (operationType.equals(OperationTypeEnum.DELETE.name())) {
                Map<String, String> deleteMap = (Map<String, String>) map.get("imageInfo");
                Long deleteImagePk = Long.parseLong(deleteMap.get("pk"));
                delete(deleteImagePk);
            }
        } catch (JsonProcessingException e) {
            log.error("Json 에러: {}", e);
        } catch (IOException e) {
            log.error("S3 에러: {}", e);
        }
    }

    @Override
    public void save(ImageDTO imageDTO) {
        try {
            String fileName = createFileName(imageDTO.getExtension());
            String path = uploadS3Service(imageDTO, fileName);
            String url = getFileURL(path);

            ImageEntity image = ImageEntity.builder()
                    .imageFile(fileName)
                    .imagePath(imageDTO.getImageType().toString())
                    .imageUrl(url)
                    .build();

            switch (imageDTO.getImageType()) {
                case FOOD -> createFoodImage(imageDTO, image);
                case FAQ -> createFAQImage(imageDTO, image);
                default -> log.error("Json 에러: imageDTO");
            }
        } catch (JsonProcessingException e) {
            log.error("Json 에러: {}", e);
        } catch (IOException e) {
            log.error("S3 저장 에러: {}", e);
        }
    }

    @Override
    public void delete(Long imagePk) {

    }

    @Override
    public void createFoodImage(ImageDTO imageDTO, ImageEntity image) {
        FoodImageEntity foodImageEntity = FoodImageEntity.builder()
                .menuPk(imageDTO.getPk())
                .image(image)
                .build();

        foodImageRepository.save(foodImageEntity);
    }

    @Override
    public void createFAQImage(ImageDTO imageDTO, ImageEntity image) {
        FAQImageEntity faqImageEntity = FAQImageEntity.builder()
                .faqPk(imageDTO.getPk())
                .image(image)
                .build();

        faqImageRepository.save(faqImageEntity);
    }

    // 이미지 업로드
    @Override
    public String uploadS3Service(ImageDTO imageDTO, String fileName) throws IOException {
        StringBuilder path = new StringBuilder();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageDTO.getSize());
        metadata.setContentType(imageDTO.getContentType());

        try (InputStream inputStream = new ByteArrayInputStream(imageDTO.getFile())) {
            path.append(imageDTO.getImageType()).append("/").append(fileName);
            amazonS3.putObject(bucket, path.toString(), inputStream, metadata);

            return path.toString();
        }
    }

    // 파일 URL
    @Override
    public String getFileURL(String path) {
        return amazonS3.generatePresignedUrl(new GeneratePresignedUrlRequest(bucket, path)).toString();
    }

    // 파일 이름 생성
    @Override
    public String createFileName(String extension) {
        StringBuilder sb = new StringBuilder();
        return String.valueOf(sb.append(UUID.randomUUID()).append(".").append(extension));
    }
}
