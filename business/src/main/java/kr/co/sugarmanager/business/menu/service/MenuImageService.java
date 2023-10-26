package kr.co.sugarmanager.business.menu.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;


public interface MenuImageService {
    int produceMessage(Long pk, String type, List<MultipartFile> multipartFile);
}
