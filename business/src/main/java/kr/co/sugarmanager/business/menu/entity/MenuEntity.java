package kr.co.sugarmanager.business.menu.entity;

import jakarta.persistence.*;
import kr.co.sugarmanager.business.global.entity.CUDEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name = "MENU")
public class MenuEntity extends CUDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_PK")
    private Long menuPk;

    @Column(name = "USER_PK")
    private Long userPk;

    @OneToMany(mappedBy = "menuEntity")
    private List<FoodEntity> foodList;

    @OneToMany(mappedBy = "menuEntity")
    private List<FoodImageEntity> foodImageList;

    public void addFoodEntity(FoodEntity foodEntity) {
        foodList.add(foodEntity);
    }

    public void addFoodImageEntity(FoodImageEntity foodImageEntity) {
        foodImageList.add(foodImageEntity);
    }
}
