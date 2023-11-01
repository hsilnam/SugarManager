package kr.co.sugarmanager.image.service;

import kr.co.sugarmanager.image.dto.ImageDTO;
import kr.co.sugarmanager.image.entity.ImageEntity;

import java.io.IOException;

public interface ImageSaveService {
    void save(String message);
    void createFoodImage(ImageDTO imageDTO, ImageEntity image);
    void createFAQImage(ImageDTO imageDTO, ImageEntity image);
    String uploadS3Service(ImageDTO imageDTO, String path) throws IOException;
    String getFileURL(String path);
    String createFileName(String extension);
}
