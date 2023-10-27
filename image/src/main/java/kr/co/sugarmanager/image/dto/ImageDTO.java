package kr.co.sugarmanager.image.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ImageDTO implements Serializable {
    private long pk;
    private String type;
    private String extension;
    private byte[] file;
}
