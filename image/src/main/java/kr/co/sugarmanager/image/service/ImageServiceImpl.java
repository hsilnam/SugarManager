package kr.co.sugarmanager.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.image.dto.ImageDTO;
import kr.co.sugarmanager.image.entity.FAQImageEntity;
import kr.co.sugarmanager.image.entity.FoodImageEntity;
import kr.co.sugarmanager.image.entity.ImageEntity;
import kr.co.sugarmanager.image.repository.FAQImageRepository;
import kr.co.sugarmanager.image.repository.FoodImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final AmazonS3 amazonS3;
    private final FoodImageRepository foodImageRepository;
    private final FAQImageRepository faqImageRepository;
    private final ObjectMapper objectMapper;
    private String path;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 카프카로부터 데이터 받아오기
    @KafkaListener(topics = "image")
    public void getMessageFromKafka(String message) {
        log.info("message: {}", message);
        try {
            ImageDTO imageDTO = objectMapper.readValue(message, ImageDTO.class);

            String fileName = uploadS3Service(imageDTO);
            String url = getFileURL();

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

    public void createFoodImage(ImageDTO imageDTO, ImageEntity image) {
        FoodImageEntity foodImageEntity = FoodImageEntity.builder()
                .menuPk(imageDTO.getPk())
                .image(image)
                .build();

        foodImageRepository.save(foodImageEntity);
    }

    public void createFAQImage(ImageDTO imageDTO, ImageEntity image) {
        FAQImageEntity faqImageEntity = FAQImageEntity.builder()
                .faqPk(imageDTO.getPk())
                .image(image)
                .build();

        faqImageRepository.save(faqImageEntity);
    }

    // 이미지 업로드
    public String uploadS3Service(ImageDTO imageDTO) throws IOException {
        String filename = createFileName(imageDTO.getExtension());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageDTO.getSize());
        metadata.setContentType(imageDTO.getContentType());

        try (InputStream inputStream = new ByteArrayInputStream(imageDTO.getFile())) {
            path = imageDTO.getImageType() + "/" + filename;

            amazonS3.putObject(bucket, path, inputStream, metadata);

            return filename;
        }
    }

    // 파일 URL
    public String getFileURL() {
        return amazonS3.generatePresignedUrl(new GeneratePresignedUrlRequest(bucket, path)).toString();
    }

    // 파일 이름 생성
    public String createFileName(String extension) {
        StringBuilder sb = new StringBuilder();
        return String.valueOf(sb.append(UUID.randomUUID()).append(".").append(extension));
    }
}
