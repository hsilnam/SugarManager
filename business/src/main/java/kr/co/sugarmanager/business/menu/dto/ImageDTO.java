package kr.co.sugarmanager.business.menu.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ImageDTO implements Serializable {
    private String pk;
    private ImageTypeEnum imageTypeEnum;
    private String extension;
    private long size;
    private String contentType;
    private byte[] file;

    public ObjectNode getObjectNode(ObjectMapper objectMapper) {
        ObjectNode imageInfoNode = objectMapper.createObjectNode();
        imageInfoNode.put("pk", this.pk);
        imageInfoNode.put("imageType", this.imageTypeEnum.name());
        imageInfoNode.put("extension", this.extension);
        imageInfoNode.put("size", this.size);
        imageInfoNode.put("contentType", this.contentType);
        imageInfoNode.put("file", this.file);
        return imageInfoNode;
    }
}
