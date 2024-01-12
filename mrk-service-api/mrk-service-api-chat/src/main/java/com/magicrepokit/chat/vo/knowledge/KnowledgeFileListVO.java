package com.magicrepokit.chat.vo.knowledge;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel(value = "知识库文件详情",description = "知识库文件详情")
public class KnowledgeFileListVO {
    /**
     * 知识库id
     */
    @ApiModelProperty(value = "知识库id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 知识库名称
     */
    @ApiModelProperty(value = "知识库名称")
    private String name;

    /**
     * 知识库描述
     */
    @ApiModelProperty(value = "知识库描述")
    private String description;

    /**
     * 向量文件ELS索引名
     */
    @ApiModelProperty(value = "向量文件ELS索引名")
    private String indexName;

    /**
     * 文件列表
     */
    @ApiModelProperty(value = "文件信息列表")
    private List<FileDetailVO> fileDetails;


    @Data
    @ApiModel(value = "文件详情",description = "文件详情")
    public static class FileDetailVO {
        /**
         * 知识库详情id
         */
        @ApiModelProperty(value = "知识库详情id")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long detailId;

        /**
         * 文件名称
         */
        @ApiModelProperty(value = "文件名称")
        private String fileName;

        /**
         * 文件类型
         */
        @ApiModelProperty(value = "文件类型")
        private String fileType;

        /**
         * 文件地址
         */
        @ApiModelProperty(value = "源文件地址")
        private String fileUrl;

        /**
         * 任务状态
         */
        @ApiModelProperty(value = "任务状态[1:未开始 2:文件分隔中 3:训练 4:完成 5:失败]")
        private Integer status;

        /**
         * 错误信息
         */
        @ApiModelProperty(value = "错误信息")
        private String errorMsg;

        /**
         * 创建时间
         */
        @ApiModelProperty(value = "创建时间")
        private LocalDateTime createTime;
    }


}
