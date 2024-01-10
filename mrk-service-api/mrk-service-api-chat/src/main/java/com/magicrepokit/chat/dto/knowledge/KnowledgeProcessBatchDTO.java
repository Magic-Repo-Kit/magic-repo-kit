package com.magicrepokit.chat.dto.knowledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel(value = "知识库文件批量处理",description = "知识库文件批量处理")
public class KnowledgeProcessBatchDTO {
    /**
     * 知识库id(必须是文件类型的id)
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 文件信息
     */
    @ApiModelProperty(value = "文件信息")
    private List<FileVO> files;


    @Data
    @ApiModel(value = "文件处理信息",description = "文件处理信息")
    public static class FileVO {
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
}
