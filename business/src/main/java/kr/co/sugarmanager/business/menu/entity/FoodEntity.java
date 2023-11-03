package kr.co.sugarmanager.business.menu.entity;

import jakarta.persistence.*;
import kr.co.sugarmanager.business.global.entity.CUDEntity;
import kr.co.sugarmanager.business.menu.dto.FoodDTO;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
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

    @Column(name = "MENU_PK")
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

    public FoodEntity(FoodDTO foodDTO) {
        setFoodName(foodDTO.getFoodName());
        setFoodCarbohydrate(foodDTO.getFoodCarbohydrate());
        setFoodProtein(foodDTO.getFoodProtein());
        setFoodDietaryFiber(foodDTO.getFoodDietaryFiber());
        setFoodVitamin(foodDTO.getFoodVitamin());
        setFoodMineral(foodDTO.getFoodMineral());
        setFoodSalt(getFoodSalt());
        setFoodSugars(foodDTO.getFoodSugars());
        setFoodCal(foodDTO.getFoodCal());
        setFoodGrams(foodDTO.getFoodGrams());
    }
}
