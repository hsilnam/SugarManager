package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.menu.dto.ImageDTO;
import kr.co.sugarmanager.business.menu.dto.ImageTypeEnum;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


public interface MenuImageService {
    void saveImage(Long pk, ImageTypeEnum imageTypeEnum, List<MultipartFile> multipartFile);
    void deleteImage(List<Long> pkList);
    ImageDTO createImageDTO(Long pk, ImageTypeEnum imageTypeEnum, MultipartFile multipartFile);
    void checkImageFileExtension(String extension);
}
