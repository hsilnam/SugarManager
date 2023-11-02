package kr.co.sugarmanager.image.service;

import kr.co.sugarmanager.image.dto.ImageDTO;
import kr.co.sugarmanager.image.dto.OperationTypeEnum;
import kr.co.sugarmanager.image.entity.ImageEntity;

import java.util.Map;

public interface ImageService {
    void service(OperationTypeEnum operationType, Map<String, Object> imageInfoMap);
    void save(ImageDTO imageDTO);
    void delete(String filePath);
    void createFoodImage(ImageDTO imageDTO, ImageEntity image);
    void createFAQImage(ImageDTO imageDTO, ImageEntity image);
}
