package kr.co.sugarmanager.business.menu.entity;

import jakarta.persistence.*;
import kr.co.sugarmanager.business.global.entity.CUDEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE MENU SET DELETED_AT = now() WHERE MENU_PK = ?")
@Where(clause = "DELETED_AT is null")
@Getter
@Table(name = "MENU")
public class MenuEntity extends CUDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_PK")
    private Long menuPk;

    @Column(name = "USER_PK")
    private Long userPk;

    @Column(name = "REGISTED_AT")
    @Builder.Default
    private LocalDateTime registedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "menuEntity")
    private List<FoodEntity> foodList;

    @OneToMany(mappedBy = "menuEntity")
    private List<FoodImageEntity> foodImageList;

    public void modifyRegistedAt(LocalDateTime registedAt) {
        this.registedAt = registedAt == null ? LocalDateTime.now() : registedAt;
    }

    public void addFoodEntity(FoodEntity foodEntity) {
        foodList.add(foodEntity);
    }

    public void addFoodImageEntity(FoodImageEntity foodImageEntity) {
        foodImageList.add(foodImageEntity);
    }
}
