package kr.co.sugarmanager.image.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.image.dto.ImageDTO;
import kr.co.sugarmanager.image.dto.OperationTypeEnum;
import kr.co.sugarmanager.image.entity.FAQImageEntity;
import kr.co.sugarmanager.image.entity.FoodImageEntity;
import kr.co.sugarmanager.image.entity.ImageEntity;
import kr.co.sugarmanager.image.repository.FAQImageRepository;
import kr.co.sugarmanager.image.repository.FoodImageRepository;
import kr.co.sugarmanager.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final FoodImageRepository foodImageRepository;
    private final FAQImageRepository faqImageRepository;

    @Override
    public void service(OperationTypeEnum operationType, Map<String, Object> imageInfoMap) {
        switch (operationType) {
            case SAVE -> {
                ImageDTO imageDTO = new ImageDTO(imageInfoMap);
                save(imageDTO);
            }
            case DELETE -> {
                String deleteImagePk = (String) imageInfoMap.get("filePath");
                delete(deleteImagePk);
            }
        }
    }

    @Override
    public void save(ImageDTO imageDTO) {
        try {
            String fileName = imageRepository.createFileName(imageDTO.getExtension());
            String path = imageRepository.uploadS3Service(imageDTO, fileName);
            String url = imageRepository.getFileURL(path);

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
    public void delete(String filePath) {
        imageRepository.deleteFile(filePath);
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
}
