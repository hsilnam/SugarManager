package kr.co.sugarmanager.image.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

@Data
@Builder(builderClassName = "ImageDtoBuilder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = ImageDTO.ImageDtoBuilder.class)
public class ImageDTO {
    private long pk;
    @JsonValue
    private ImageTypeEnum imageType;
    private String extension;
    private long size;
    private String contentType;
    private byte[] file;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ImageDtoBuilder {}
}
