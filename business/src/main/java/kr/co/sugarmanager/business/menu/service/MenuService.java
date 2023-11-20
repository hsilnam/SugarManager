package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.menu.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MenuService {
    MenuSaveDTO.Response saveImage(Long userPk, Long menuPk, List<MultipartFile> imageFiles);

    MenuSaveDTO.Response saveContent(Long userPk, MenuSaveDTO.Request request);

    MenuSaveDTO.Response save(Long userPk, List<MultipartFile> imageFiles, MenuSaveDTO.Request request);

    MenuDeleteDTO.Response delete(Long userPk, MenuDeleteDTO.Request request);

    MenuSelectDTO.Response select(MenuSelectDTO.Request request);

    MenuEditDTO.Response edit(MenuEditDTO.Request request);

    MenuDayDTO.Response selectDay(Long userPk, String nickname, int year, int month, int day);

    MenuPeriodDTO.Response selectPeriod(Long userPk, String targetUserNickname, String startDate, String endDate, int page);
}
