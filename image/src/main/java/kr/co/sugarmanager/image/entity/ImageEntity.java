package kr.co.sugarmanager.image.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ImageEntity {

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "IMAGE_PATH")
    private String imagePath;

    @Column(name = "IMAGE_FILE")
    private String imageFile;
}
