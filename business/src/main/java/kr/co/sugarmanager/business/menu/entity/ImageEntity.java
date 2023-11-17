package kr.co.sugarmanager.business.menu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.Where;

@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ImageEntity {

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "IMAGE_PATH")
    private String imagePath;

    @Column(name = "IMAGE_FILE")
    private String imageFile;
}
