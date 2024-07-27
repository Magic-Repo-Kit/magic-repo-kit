package com.magicrepokit.chat;

import dev.langchain4j.model.input.structured.StructuredPrompt;
import lombok.Data;

import java.util.List;

/**
 * 做菜助手提示词
 */
@Data
@StructuredPrompt("创建一个菜名为{{dish}}的做法，包含以下是食材原材料，调味品不算：{{ingredients}},请给出具体的步骤，以及食材配比和调味品的重量!")
public class CookingAssistant {
    /**
     * 原材料
     */
    private List<String> ingredients;

    /**
     * 菜名
     */
    private String dish;
}
