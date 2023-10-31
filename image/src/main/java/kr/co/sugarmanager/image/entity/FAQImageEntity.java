package kr.co.sugarmanager.image.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FAQ_IMAGE")
public class FAQImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAQ_IMAGE_PK")
    private Long faqImagePk;

    @Column(name = "FAQ_PK")
    private Long faqPk;

    @Embedded
    private ImageEntity image;
}
