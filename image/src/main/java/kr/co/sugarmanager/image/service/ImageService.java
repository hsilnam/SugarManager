package kr.co.sugarmanager.image.service;

import kr.co.sugarmanager.image.dto.ImageDTO;
import kr.co.sugarmanager.image.entity.ImageEntity;

import java.io.IOException;

public interface ImageService {
    void getMessage(String message);
    void createFoodImage(ImageDTO imageDTO, ImageEntity image);
    void createFAQImage(ImageDTO imageDTO, ImageEntity image);
    String uploadS3Service(ImageDTO imageDTO) throws IOException;
    String getFileURL();
    String createFileName(String extension);
}
