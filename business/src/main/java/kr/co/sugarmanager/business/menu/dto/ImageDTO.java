package kr.co.sugarmanager.business.menu.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ImageDTO implements Serializable {
    private long pk;
    private String type;
    private byte[] file;
}
