package kr.co.sugarmanager.business.menu.controller;

import kr.co.sugarmanager.business.menu.dto.MenuDeleteDTO;
import kr.co.sugarmanager.business.menu.dto.MenuSaveDTO;
import kr.co.sugarmanager.business.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Slf4j
public class MenuController {
    private final MenuService menuService;

    @PostMapping(value = "/save", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MenuSaveDTO.Response> save(
            @RequestHeader("Authorization") String auth,
            @RequestPart(value = "file", required = false) List<MultipartFile> imageFile,
            @Validated @RequestPart MenuSaveDTO.Request menuDto) {
        log.info("MenuSave - Authorization: {}, menuDto: {}", auth, menuDto.getFoods());
        Long userPk = 1L;       // TODO: 서버간 통신 필요
        return new ResponseEntity<>(menuService.save(userPk, imageFile, menuDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/delete", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<MenuDeleteDTO.Response> delete(
            @RequestHeader("Authorization") String auth,
            @Validated @RequestBody MenuDeleteDTO.Request menuPk) {
        log.info("MenuDelete - Authorization: {}, menuDto: {}", auth, menuPk.getMenuPk());
        Long userPk = 1L;       // TODO: 서버간 통신 필요
        return new ResponseEntity<>(menuService.delete(userPk, menuPk), HttpStatus.OK);
    }
}
