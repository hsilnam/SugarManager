package kr.co.sugarmanager.image.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.util.Map;

@Data
@JsonDeserialize(builder = ImageDTO.ImageDtoBuilder.class)
public class ImageDTO {

    private String pk;

    @JsonValue
    private ImageTypeEnum imageType;
    private String extension;
    private int size;
    private String contentType;
    private byte[] file;

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFile(String file) {
        this.file = file.getBytes();
    }

    public ImageDTO(Map<String,Object> map) {
        this(map.get("pk"),
                map.get("imageType"),
                map.get("extension"),
                map.get("size"),
                map.get("contentType"),
                map.get("file"));
    }

    public ImageDTO(Object pk, Object imageType, Object extension, Object size, Object contentType, Object file) {
        setPk((String) pk);
        setImageType(ImageTypeEnum.valueOf((String) imageType));
        setExtension((String) extension);
        setSize((Integer) size);
        setContentType((String) contentType);
        setFile((String) file);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ImageDtoBuilder {}
}
