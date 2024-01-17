package com.magicrepokit.chat.controller;


import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.chat.constant.GptModel;
import com.magicrepokit.chat.vo.gptModel.GptModelListVO;
import com.magicrepokit.common.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("model")
@AllArgsConstructor
@Api(value = "GPT模型接口", tags = "GPT模型接口")
public class GptModelController {

    /**
     * GPT模型列表
     * @param type 类型[空:全部,1:文本,2:向量,3:图像,4:文本审核]
     * @return 模型列表
     */
    @ApiOperation(value = "GPT模型列表", notes = "GPT模型列表")
    @GetMapping("/list")
    public R<List<GptModelListVO>> list(@ApiParam("类型:[空:全部,1:文本,2:向量,3:图像,4:文本审核]") @RequestParam(required = false) Integer type){
        if(ObjectUtil.isEmpty(type)){
            type = 0;
        }
        List<GptModelListVO> list;
        switch (type){
            case 1:
                list = convertList(GptModel.getLanguageModelName());
                break;
            case 2:
                list = convertList(GptModel.getEmbeddingModelName());
                break;
            case 3:
                list = convertList(GptModel.getImageModelName());
                break;
            case 4:
                list = convertList(GptModel.getModerationModelName());
                break;
            default:
                list = convertList(GptModel.getAllModelName());
        }

        return R.data(list);
    }

    private List<GptModelListVO> convertList(List<String> list){
        return list.stream().map(GptModelListVO::new).collect(Collectors.toList());
    }
}
