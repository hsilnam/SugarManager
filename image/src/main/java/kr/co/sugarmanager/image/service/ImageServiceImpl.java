package kr.co.sugarmanager.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.image.dto.ImageDTO;
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
    private String path;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 카프카로부터 데이터 받아오기
    @KafkaListener(topics = "image")
    public void getMessageFromKafka(String message) {
        log.info("message: {}", message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ImageDTO imageDTO = objectMapper.readValue(message, ImageDTO.class);
            String fileName = uploadS3Service(imageDTO);
            String url = getFileURL();
            log.info("fileName: {}", fileName);
            log.info("url: {}", url);
        } catch (JsonProcessingException e) {
            log.error("Json 에러: {}", e);
        } catch (IOException e) {
            log.error("S3 저장 에러: {}", e);
        }
    }

    // 이미지 업로드
    public String uploadS3Service(ImageDTO imageDTO) throws IOException {
        String filename = createFileName(imageDTO.getExtension());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageDTO.getSize());
        metadata.setContentType(imageDTO.getContentType());

        try (InputStream inputStream = new ByteArrayInputStream(imageDTO.getFile())) {
            path = imageDTO.getType() + "/" + filename;

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
