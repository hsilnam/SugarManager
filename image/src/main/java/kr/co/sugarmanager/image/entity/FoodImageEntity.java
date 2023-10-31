package kr.co.sugarmanager.image.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FOOD_IMAGE")
public class FoodImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOOD_IMAGE_PK")
    private Long foodImagePk;

    @Column(name = "MENU_PK")
    private Long menuPk;

    @Embedded
    private ImageEntity image;
}
