package kr.co.sugarmanager.business.menu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@SQLDelete(sql = "UPDATE FOOD_IMAGE SET DELETED_AT = now() WHERE FOOD_IMAGE_PK = ?")
@Where(clause = "DELETED_AT is null")
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
