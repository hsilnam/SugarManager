package kr.co.sugarmanager.business.menu.entity;

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
@Table(name = "FOOD_IMAGE")
public class FoodImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOOD_IMAGE_PK")
    private Long foodImagePk;

    @Column(name = "MENU_PK", insertable=false, updatable=false)
    private String menuPk;

    @Embedded
    private ImageEntity image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_PK")
    private MenuEntity menuEntity;

    public void setMenuEntity(FoodEntity foodEntity) {
        if (this.menuEntity != null) {
            this.menuEntity.getFoodList().remove(this);
        }
        this.menuEntity = menuEntity;
        menuEntity.getFoodImageList().add(this);
    }
}
