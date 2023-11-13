package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.menu.dto.MenuDeleteDTO;
import kr.co.sugarmanager.business.menu.dto.MenuEditDTO;
import kr.co.sugarmanager.business.menu.dto.MenuSaveDTO;
import kr.co.sugarmanager.business.menu.dto.MenuSelectDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MenuService {
    MenuSaveDTO.Response save(Long userPk, List<MultipartFile> imageFiles, MenuSaveDTO.Request request);
    MenuDeleteDTO.Response delete(Long userPk, MenuDeleteDTO.Request request);
    MenuSelectDTO.Response select(MenuSelectDTO.Request request);
    MenuEditDTO.Response edit(MenuEditDTO.Request request);
}
