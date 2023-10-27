package kr.co.sugarmanager.userservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

import java.util.Random;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "GROUP")
@SQLDelete(sql = "UPDATE SET DELETED_AT ON GROUP WHERE GROUP_PK = ?")
public class GroupEntity extends CUDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_PK")
    private long pk;

    @Column(name = "GROUP_CODE", nullable = false, length = 10)
    private String groupCode;

    public String generateGroupCode() {
        Random random = new Random(System.currentTimeMillis());
        int numberOffset = 48;
        int lowerOffset = 65;
        int upperOffset = 97;

        int[] offsets = {numberOffset, lowerOffset, upperOffset};
        int[] sizes = {10, 26, 26};

        StringBuffer sb = new StringBuffer();
        while (sb.length() < 10) {
            int offsetIndex = random.nextInt(offsets.length);

            int sizeIndex = random.nextInt(sizes[offsetIndex]);

            sb.append((char) (offsets[offsetIndex] + sizeIndex));
        }
        return sb.toString();
    }
}
