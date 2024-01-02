package com.magicrepokit.chat.controller;

import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.dto.KnowledgePageDTO;
import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.chat.vo.KnowledgePageVO;
import com.magicrepokit.chat.vo.KnowledgePageDT;
import com.magicrepokit.chat.vo.KnowledgeVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 知识库管理
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("/knowledge")
@AllArgsConstructor
@Api(value = "知识库管理", tags = "知识库管理接口")
public class KnowledgeController {
    private final IKnowledgeService knowledgeService;

    /**
     * 建立文件夹或文件
     */
    @PostMapping("/create")
    @ApiOperation(value = "建立文件夹或文件")
    public R<KnowledgeVO> create(@RequestBody KnowledgeCreateDTO createDTO){
        return R.data(knowledgeService.create(createDTO));
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询")
    public R<PageResult<KnowledgePageVO>> page(KnowledgePageDTO knowledgePageDTO){
        return R.data(knowledgeService.page(knowledgePageDTO));
    }

}
