package kr.co.sugarmanager.image.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sugarmanager.image.dto.ImageDTO;
import kr.co.sugarmanager.image.dto.OperationTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ImageServiceTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 메시지_역직렬화_성공() throws Exception {
        //given
        String message = "{\"operationTypeEnum\": \"SAVE\",\"imageInfo\": {\"pk\": \"1\",\"imageType\": \"FOOD\",\"extension\": \"jpeg\",\"size\": \"14936\",\"contentType\": null,\"file\": \"fileTest\"}}";
        //when
        Map<String, Object> map = objectMapper.readValue(message, Map.class);
        String operationType = (String) map.get("operationTypeEnum");
        System.out.println(operationType);

        System.out.println(map.get("imageInfo").getClass());
        if (operationType.equals(OperationTypeEnum.SAVE.name())){
            Map<String, Object> imageInfoMap = (Map<String, Object>) map.get("imageInfo");
            System.out.println(imageInfoMap);
            ImageDTO imageDTO = new ImageDTO(imageInfoMap);
            assertThat(imageDTO.getPk()).isNotNull();
        } else if (operationType.equals(OperationTypeEnum.DELETE.name())){
            Map<String, String> deleteMap = (Map<String, String>) map.get("imageInfo");
            Long deleteImagePk = Long.parseLong(deleteMap.get("pk"));
            assertThat(deleteImagePk).isNotNull();
        }
    }
}
