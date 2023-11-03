package kr.co.sugarmanager.business.menu.service;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.menu.dto.FoodDTO;
import kr.co.sugarmanager.business.menu.dto.ImageTypeEnum;
import kr.co.sugarmanager.business.menu.dto.MenuSaveDTO;
import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import kr.co.sugarmanager.business.menu.repository.FoodRepository;
import kr.co.sugarmanager.business.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService{
    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final MenuImageService menuImageService;
    @Override
    @Transactional
    public MenuSaveDTO.Response save(Long userPk, List<MultipartFile> imageFiles, MenuSaveDTO.Request request) {
        MenuEntity menuEntity = MenuEntity.builder()
                .userPk(userPk)
                .foodList(new ArrayList<>())
                .foodImageList(new ArrayList<>())
                .build();
        MenuEntity menu = menuRepository.save(menuEntity);

        for(FoodDTO food: request.getFoodList()) {
            FoodEntity foodEntity = new FoodEntity(food);
            foodEntity.setMenuPk(menu.getMenuPk());
            foodEntity.setMenuEntity(menu);
            menuEntity.addFoodEntity(foodEntity);
            foodRepository.save(foodEntity);
        }

        menuImageService.saveImage(menu.getMenuPk(), ImageTypeEnum.FOOD, imageFiles);

        return MenuSaveDTO.Response
                .builder()
                .success(true)
                .build();
    }
}
