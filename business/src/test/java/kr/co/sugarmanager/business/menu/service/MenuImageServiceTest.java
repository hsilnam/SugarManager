package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.global.exception.ValidationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class MenuImageServiceTest {
    @Autowired
    private MenuImageService menuImageService;

    @Test
    void 이미지_데이터_전송() throws Exception {
        //given
        Long pk = 1L;
        String type = "example";

        List<MultipartFile> multipartFiles = new ArrayList<>();

        String base = "src/test/java/kr/co/sugarmanager/business/menu/service/asset";
        Path logoPath = Paths.get(base, "logo.png");
        InputStream logoInputStream = new FileInputStream(logoPath.toFile());
        MockMultipartFile logoFile = new MockMultipartFile("logo.png", logoInputStream);

        Path catPath = Paths.get(base, "cat.jpeg");
        InputStream catInputStream = new FileInputStream(catPath.toFile());
        MockMultipartFile catFile = new MockMultipartFile("cat.jpeg", catInputStream);

        multipartFiles.add(logoFile);
        multipartFiles.add(catFile);

        //when
        int sendImageCount = menuImageService.produceMessage(pk, type, multipartFiles);
        //then

        assertEquals(multipartFiles.size(), sendImageCount);
    }

    @Test
    void 이미지_확장자_오류() throws Exception {
        //given
        Long pk = 1L;
        String type = "example";

        List<MultipartFile> multipartFiles = new ArrayList<>();

        String base = "src/test/java/kr/co/sugarmanager/business/menu/service/asset";
        Path catPath = Paths.get(base, "cat.jjj");
        InputStream catInputStream = new FileInputStream(catPath.toFile());
        MockMultipartFile catFile = new MockMultipartFile("cat.jjj", catInputStream);

        multipartFiles.add(catFile);

        //when

        //then
        assertThrows(ValidationException.class, () -> {
            int sendImageCount = menuImageService.produceMessage(pk, type, multipartFiles);
        });
    }
}
