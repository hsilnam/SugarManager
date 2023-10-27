package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.menu.dto.ImageDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
public class MenuImageServiceTest {
    @Autowired
    private MenuService menuImageService;

    @Test
    void 이미지_전송을_위한_ImageDTO_생성_성공() throws Exception{
        Long pk = 1L;
        String type = "example";
        List<MultipartFile> multipartFiles = new ArrayList<>();

        String base = "src/test/java/kr/co/sugarmanager/business/menu/service/asset";
        Path logoPath = Paths.get(base, "logo.png");
        InputStream logoInputStream = new FileInputStream(logoPath.toFile());
        MockMultipartFile logoFile = new MockMultipartFile("logo.png", logoInputStream);

        ImageDTO imageDTO = menuImageService.createImageDTO(pk, type, logoFile);

        assertArrayEquals(logoFile.getBytes(), imageDTO.getFile());
    }

    @Test
    void 이미지_전송을_위한_ImageDTO_생성_실패() throws Exception{
        Long pk = 1L;
        String type = "example";
        List<MultipartFile> multipartFiles = new ArrayList<>();

        String base = "src/test/java/kr/co/sugarmanager/business/menu/service/asset";
        Path logoPath = Paths.get(base, "cat.jjj");
        InputStream logoInputStream = new FileInputStream(logoPath.toFile());
        MockMultipartFile logoFile = new MockMultipartFile("logo.jjj", logoInputStream);

        assertThrows(ValidationException.class, () -> {
            menuImageService.createImageDTO(pk, type, logoFile);
        });
    }

    @Test
    void 이미지_데이터_전송() throws Exception {
        //given
        Long pk = 1L;
        String type = "example";

        List<MultipartFile> multipartFiles = new ArrayList<>();

        String base = "src/test/java/kr/co/sugarmanager/business/menu/service/asset";
        Path catPath = Paths.get(base, "cat.jpeg");
        InputStream catInputStream = new FileInputStream(catPath.toFile());
        MockMultipartFile catFile = new MockMultipartFile("cat.jpeg", catInputStream);

        for (int i = 0; i < 2; i++) {
            multipartFiles.add(catFile);
        }

        menuImageService.produceMessage(pk, type, multipartFiles);
    }
}
