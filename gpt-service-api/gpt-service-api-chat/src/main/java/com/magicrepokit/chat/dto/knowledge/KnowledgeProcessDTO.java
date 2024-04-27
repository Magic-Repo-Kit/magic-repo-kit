package com.magicrepokit.chat.dto.knowledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel(value = "知识库文件处理",description = "知识库文件处理")
public class KnowledgeProcessDTO {
    /**
     * 知识库id(必须是文件类型的id)
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件Url")
    private String fileUrl;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String fileName;
}
