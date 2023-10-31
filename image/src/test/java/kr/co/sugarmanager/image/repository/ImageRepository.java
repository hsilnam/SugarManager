package kr.co.sugarmanager.image.repository;

import kr.co.sugarmanager.image.entity.FoodImageEntity;
import kr.co.sugarmanager.image.entity.ImageEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ImageRepository {
    @Autowired
    private FoodImageRepository foodImageRepository;

    @Test
    void Food_Image_저장() throws Exception {
        //given
        ImageEntity image = ImageEntity.builder()
                .imageFile("cat.jpg")
                .imagePath("food")
                .imageUrl("https://sugarmanager.s3.ap-northeast-2.amazonaws.com/example/60339f23-116e-4a03-a75b-54a02e7ba524.jpeg")
                .build();
        FoodImageEntity foodImageEntity = FoodImageEntity.builder()
                .menuPk(1L)
                .image(image)
                .build();

        //when
        FoodImageEntity expectedValue = foodImageRepository.save(foodImageEntity);

        //then
        assertThat(expectedValue.getFoodImagePk()).isNotNull();
    }
}
