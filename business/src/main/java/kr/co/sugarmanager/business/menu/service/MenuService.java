package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.menu.dto.ImageDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


public interface MenuService {
    void produceMessage(Long pk, String type, List<MultipartFile> multipartFile);
    ImageDTO createImageDTO(Long pk, String type, MultipartFile multipartFile);
}
