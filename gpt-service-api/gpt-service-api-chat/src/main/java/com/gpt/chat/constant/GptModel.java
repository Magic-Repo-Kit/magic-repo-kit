package com.gpt.chat.constant;

import cn.hutool.core.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

public enum GptModel {
    GPT_3_5_TURBO("gpt-3.5-turbo", "gpt-3.5-turbo", 1001),
    GPT_3_5_TURBO_0301("gpt-3.5-turbo-0301", "gpt-3.5-turbo-0301", 1002),
    GPT_3_5_TURBO_0613("gpt-3.5-turbo-0613", "gpt-3.5-turbo-0613", 1003),
    GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106", "gpt-3.5-turbo-1106", 1004),
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k", "gpt-3.5-turbo-16k", 1005),
    GPT_3_5_TURBO_16K_0613("gpt-3.5-turbo-16k-0613", "gpt-3.5-turbo-16k-0613", 1006),
    GPT_4("gpt-4", "gpt-4", 1007),
    GPT_4_0314("gpt-4-0314", "gpt-4-0314", 1008),
    GPT_4_0613("gpt-4-0613", "gpt-4-0613", 1009),
    GPT_4_32K("gpt-4-32k", "gpt-4-32k", 1010),
    GPT_4_32K_0314("gpt-4-32k-0314", "gpt-4-32k-0314", 1011),
    GPT_4_32K_0613("gpt-4-32k-0613", "gpt-4-32k-0613", 1012),
    GPT_4_1106_PREVIEW("gpt-4-1106-preview", "gpt-4-1106-preview", 1013),
    GPT_4_VISION_PREVIEW("gpt-4-vision-preview", "gpt-4-vision-preview", 1014),
    GPT_3_5_TURBO_INSTRUCT("gpt-3.5-turbo-instruct", "gpt-3.5-turbo-instruct", 1015),
    TEXT_EMBEDDING_ADA_002("text-embedding-ada-002", "TEXT_EMBEDDING_ADA_002",1016),
    TEXT_MODERATION_STABLE("text-moderation-stable", "TEXT_MODERATION_STABLE",1017),
    TEXT_MODERATION_LATEST("text-moderation-latest", "TEXT_MODERATION_LATEST",1018),
    DALL_E_2("dall-e-2", "DALL_E_2",1019),
    DALL_E_3("dall-e-3", "DALL_E_3",1020),
    LLAMA2("llama2", "llama2", 1021);


    private String modelName;

    private String acutualModelName;
    private Integer code;

    GptModel(String modelName, String actualModelName,Integer code) {
        this.modelName = modelName;
        this.code = code;
        this.acutualModelName = actualModelName;
    }

    public String getModelName() {
        return modelName;
    }

    public Integer getCode() {
        return code;
    }

    public static GptModel getByCode(Integer code) {
        for (GptModel gptModel : GptModel.values()) {
            if (gptModel.getCode().equals(code)) {
                return gptModel;
            }
        }
        return null;
    }

    public static GptModel getByModelName(String modelName) {
        if(ObjectUtil.isEmpty(modelName)){
            return null;
        }
        for (GptModel gptModel : GptModel.values()) {
            if (gptModel.getModelName().equals(modelName)) {
                return gptModel;
            }
        }
        return null;
    }

    public String getAcutualModelName() {
        return acutualModelName;
    }

    public static List<String> getAllModelName() {
        List<String> list = new ArrayList<>();
        for (GptModel gptModel : GptModel.values()) {
            list.add(gptModel.getModelName());
        }
        return list;
    }

    public static List<String> getLanguageModelName() {
        List<String> list = new ArrayList<>();
        for (GptModel gptModel : GptModel.values()) {
            if(gptModel.getModelName().contains("gpt")){
                list.add(gptModel.getModelName());
            }
        }
        return list;
    }

    public static List<String> getEmbeddingModelName() {
        List<String> list = new ArrayList<>();
        for (GptModel gptModel : GptModel.values()) {
            if(gptModel.getModelName().contains("text-embedding")){
                list.add(gptModel.getModelName());
            }
        }
        return list;
    }

    public static List<String> getModerationModelName() {
        List<String> list = new ArrayList<>();
        for (GptModel gptModel : GptModel.values()) {
            if(gptModel.getModelName().contains("text-moderation")){
                list.add(gptModel.getModelName());
            }
        }
        return list;
    }

    public static List<String> getImageModelName() {
        List<String> list = new ArrayList<>();
        for (GptModel gptModel : GptModel.values()) {
            if(gptModel.getModelName().contains("dall-e")){
                list.add(gptModel.getModelName());
            }
        }
        return list;
    }
}
