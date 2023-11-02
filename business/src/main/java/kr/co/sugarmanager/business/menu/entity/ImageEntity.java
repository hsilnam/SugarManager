package kr.co.sugarmanager.business.menu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
