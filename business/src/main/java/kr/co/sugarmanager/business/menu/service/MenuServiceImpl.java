package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.bloodsugar.dto.BLOODSUGARCATEGORY;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.menu.dto.*;
import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import kr.co.sugarmanager.business.menu.entity.FoodImageEntity;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import kr.co.sugarmanager.business.menu.exception.MenuException;
import kr.co.sugarmanager.business.menu.repository.FoodRepository;
import kr.co.sugarmanager.business.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final MenuImageService menuImageService;
    private final BloodSugarRepository bloodSugarRepository;

    @Override
    @Transactional
    public MenuSaveDTO.Response save(Long userPk, List<MultipartFile> imageFiles, MenuSaveDTO.Request request) {
        if (request.getFoods() == null || request.getFoods().size() == 0) {
            throw new ValidationException(ErrorCode.MISSING_INPUT_VALUE);
        }

        MenuEntity menuEntity = MenuEntity.builder()
                .userPk(userPk)
                .foodList(new ArrayList<>())
                .foodImageList(new ArrayList<>())
                .build();
        MenuEntity menu = menuRepository.save(menuEntity);

        for (FoodDTO food : request.getFoods()) {
            FoodEntity foodEntity = new FoodEntity(food);
            foodEntity.setMenuEntity(menu);
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

    @Transactional
    @Override
    public MenuDeleteDTO.Response delete(Long userPk, MenuDeleteDTO.Request request) {
        Optional<MenuEntity> menu = menuRepository.findByMenuPkAndUserPk(Long.valueOf(request.getMenuPk()), userPk);
        if (!menu.isPresent()) throw new MenuException(ErrorCode.HANDLE_ACCESS_DENIED);

        menuRepository.delete(menu.get());
        return MenuDeleteDTO.Response
                .builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MenuSelectDTO.Response select(MenuSelectDTO.Request request) {
        Long userPk = request.getUserPk();
        Long menuPk = request.getMenuPk();

        // TODO: 내 그룹원이 아닌 다른 사람의 메뉴 조회 할 경우(403 Forbidden) 체크 및 처리 필요
        Optional<MenuEntity> menuOptional = menuRepository.findByMenuPkAndUserPk(menuPk, userPk);
        if (!menuOptional.isPresent()) throw new MenuException(ErrorCode.MENU_NOT_FOUND_ERROR);

        MenuEntity menu = menuOptional.get();
        List<FoodImageEntity> foodImages = menu.getFoodImageList();
        ArrayList<MenuSelectDTO.MenuImage> repFoodImages = (foodImages == null) ? new ArrayList<>() : new ArrayList<>(foodImages.stream().map(foodImage -> MenuSelectDTO.MenuImage.builder()
                .menuImagePk(foodImage.getFoodImagePk())
                .menuImageUrl(foodImage.getImage().getImageUrl())
                .build()
        ).toList());

        LocalDateTime createdAt = menu.getCreatedAt();
        LocalDateTime threeHoursBefore = createdAt.minusHours(3);
        LocalDateTime threeHoursAfter = createdAt.plusHours(3);

        BloodSugarEntity beforeBloodSuger = bloodSugarRepository.findOneByUserPkAndCategoryAndCreatedAt(userPk, BLOODSUGARCATEGORY.BEFORE.name(), threeHoursBefore, createdAt).orElse(null);
        System.out.println(beforeBloodSuger);
        BloodSugarEntity afterBloodSuger = bloodSugarRepository.findOneByUserPkAndCategoryAndCreatedAt(userPk, BLOODSUGARCATEGORY.AFTER.name(), createdAt, threeHoursAfter).orElse(null);
        MenuSelectDTO.BloodSugar repBloodSugar = MenuSelectDTO.BloodSugar.builder()
                .beforeLevel((beforeBloodSuger != null) ? beforeBloodSuger.getLevel() : null)
                .afterLevel((afterBloodSuger != null) ? afterBloodSuger.getLevel() : null)
                .build();

        List<FoodEntity> foods = menu.getFoodList();
        ArrayList<MenuSelectDTO.Food> repFoods = (foods == null) ? new ArrayList<>() : new ArrayList<>(foods.stream().map(food -> MenuSelectDTO.Food.builder()
                .foodPk(food.getFoodPk())
                .foodName(food.getFoodName())
                .foodCal(food.getFoodCal())
                .foodGrams(food.getFoodGrams())
                .foodCarbohydrate(food.getFoodCarbohydrate())
                .foodProtein(food.getFoodProtein())
                .foodDietaryFiber(food.getFoodDietaryFiber())
                .foodVitamin(food.getFoodVitamin())
                .foodMineral(food.getFoodMineral())
                .foodSalt(food.getFoodSalt())
                .foodSugars(food.getFoodSugars())
                .build()
        ).toList());

        MenuSelectDTO.ReturnResponse returnResponse = MenuSelectDTO.ReturnResponse.builder()
                .menuPk(menuPk)
                .menuImages(repFoodImages)
                .bloodSugar(repBloodSugar)
                .foods(repFoods)
                .build();

        return MenuSelectDTO.Response
                .builder()
                .success(true)
                .response(returnResponse)
                .build();
    }
}
