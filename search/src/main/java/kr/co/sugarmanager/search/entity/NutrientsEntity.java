package kr.co.sugarmanager.search.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "NUTRIENTS")
public class NutrientsEntity {
    @Id
    @Column(name = "NUTRIENTS_PK")
    private Long nutrientsPk;
    @Column(name = "NUTRIENTS_NAME")
    private String nutrientsName;
    @Column(name = "NUTRIENTS_FROM")
    private String nutrientsFrom;
    @Column(name = "NUTRIENTS_AMOUNT")
    private int nutrientsAmount;
    @Column(name = "NUTRIENTS_UNIT")
    private String nutrientsUnit;
    @Column(name = "NUTRIENTS_KCAL")
    private Float nutrientsKcal;
    @Column(name = "NUTRIENTS_PROTEIN")
    private Float nutrientsProtein;
    @Column(name = "NUTRIENTS_FAT")
    private Float nutrientsFat;
    @Column(name = "NUTRIENTS_CH")
    private Float nutrientsCh;
    @Column(name = "NUTRIENTS_SUGAR")
    private Float nutrientsSugar;
    @Column(name = "NUTRIENTS_ORG")
    private String nutrientsOrg;
}
