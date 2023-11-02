package kr.co.sugarmanager.business.menu.entity;

import jakarta.persistence.*;
import kr.co.sugarmanager.business.global.entity.CUDEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FOOD")
public class FoodEntity extends CUDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOOD_PK")
    private Long foodPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_PK")
    private MenuEntity menuEntity;

    @Column(name = "MENU_PK", insertable=false, updatable=false)
    private Long menuPk;

    @Column(name = "FOOD_NAME")
    private String foodName;

    @Column(name = "FOOD_CARBOHYDRATE")
    private float foodCarbohydrate;

    @Column(name = "FOOD_PROTEIN")
    private float foodProtein;

    @Column(name = "FOOD_DIETARY_FIBER")
    private float foodDietaryFiber;

    @Column(name = "FOOD_VITAMIN")
    private float foodVitamin;

    @Column(name = "FOOD_MINERAL")
    private float foodMineral;

    @Column(name = "FOOD_SALT")
    private float foodSalt;

    @Column(name = "FOOD_SUGARS")
    private float foodSugars;

    @Column(name = "FOOD_CAL")
    private float foodCal;

    @Column(name = "FOOD_GRAMS")
    private float foodGrams;
}
