package kr.co.sugarmanager.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 카프카로부터 데이터 받아오기
    @KafkaListener(topics = "image")
    public void getMessageFromKafka(String message) {
        log.info("message: {}", message);

    }

    public String S3UploadService(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return amazonS3.getUrl(bucket, originalFilename).toString();
    }

    public StringBuilder createFileName(String extension) {
        StringBuilder sb = new StringBuilder();
        return sb.append(UUID.randomUUID()).append(".").append(extension);
    }
}
