package kr.co.sugarmanager.image.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public enum ImageTypeEnum implements Serializable {
    FOOD("FOOD"),
    FAQ("FAQ");

    private final String imageType;

    @JsonCreator
    public static ImageTypeEnum fromTypeEnum(String val){
        for(ImageTypeEnum imageType : ImageTypeEnum.values()){
            if(imageType.name().equals(val)){
                return imageType;
            }
        }
        return null;
    }

    public String toString() {
        return this.imageType;
    }
}
