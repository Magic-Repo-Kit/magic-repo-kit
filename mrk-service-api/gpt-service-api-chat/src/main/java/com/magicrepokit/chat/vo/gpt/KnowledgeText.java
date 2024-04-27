package com.magicrepokit.chat.vo.gpt;

import dev.langchain4j.data.segment.TextSegment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ApiModel(value = "KnowledgeText",description = "知识库文本")
public class KnowledgeText {
    /**
     * 文本
     */
    @ApiModelProperty(value = "文本")
    private String text;
    /**
     * 元数据
     */
    @ApiModelProperty(value = "元数据")
    private Metadata metadata;

    @Data
    @ApiModel(value = "Metadata",description = "元数据")
    public static class Metadata {
        private Map<String, String> metadata;

        public static Metadata of(dev.langchain4j.data.document.Metadata me) {
            Metadata metadata = new Metadata();
            metadata.setMetadata(me.asMap());
            return metadata;
        }
    }

    public static KnowledgeText of(TextSegment textSegment) {
        KnowledgeText knowledgeText = new KnowledgeText();
        knowledgeText.setText(textSegment.text());
        knowledgeText.setMetadata(Metadata.of(textSegment.metadata()));
        return knowledgeText;
    }

    public static List<KnowledgeText> of(List<TextSegment> textSegments) {
        return textSegments.stream().map(KnowledgeText::of).collect(Collectors.toList());
    }
}
