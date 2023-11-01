package kr.co.sugarmanager.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "USER_IMAGE")
@SQLDelete(sql = "UPDATE SET DELETED_AT ON USER_IMAGE WHERE IMAGE_PK = ?")
public class UserImageEntity extends CDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_PK")
    private long pk;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "IMAGE_PATH")
    private String imagePath;

    @Column(name = "IMAGE_FILE")
    private String imageFile;

    //Relation
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PK")
    private UserEntity user;

    //Methods
    protected void setUser(UserEntity user) {
        this.user = user;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
