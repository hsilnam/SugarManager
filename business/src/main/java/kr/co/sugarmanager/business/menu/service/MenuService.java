package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.menu.dto.ImageDTO;
import kr.co.sugarmanager.business.menu.dto.ImageTypeEnum;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


public interface MenuService {
    void produceMessage(Long pk, ImageTypeEnum imageTypeEnum, List<MultipartFile> multipartFile);
    ImageDTO createImageDTO(Long pk, ImageTypeEnum imageTypeEnum, MultipartFile multipartFile);
}
