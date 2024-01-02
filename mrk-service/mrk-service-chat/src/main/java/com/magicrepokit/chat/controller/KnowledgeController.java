package com.magicrepokit.chat.controller;

import com.magicrepokit.chat.dto.KnowledgeCreateDTO;
import com.magicrepokit.chat.dto.KnowledgeListDTO;
import com.magicrepokit.chat.dto.KnowledgeMoveDTO;
import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.chat.vo.KnowledgeListVO;
import com.magicrepokit.chat.vo.KnowledgePathVO;
import com.magicrepokit.chat.vo.KnowledgeVO;
import com.magicrepokit.common.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 文件或文件夹列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "文件或文件夹列表")
    public R<List<KnowledgeListVO>> list(KnowledgeListDTO knowledgeListDTO){
        return R.data(knowledgeService.list(knowledgeListDTO));
    }

    /**
     * 根据父节点id查询当前路径
     */
    @GetMapping("/list-path-by-parent-id")
    @ApiOperation(value = "根据父节点id查询当前路径")
    public R<List<KnowledgePathVO>> listPathByParentId(Long parentId){
        return R.data(knowledgeService.listPathByParentId(parentId));
    }

    /**
     * 移动文件或文件夹
     */
    @PostMapping("/move")
    @ApiOperation(value = "移动文件或文件夹")
    public R<Boolean> move(@RequestBody KnowledgeMoveDTO knowledgeMoveDTO){
        return R.status(knowledgeService.move(knowledgeMoveDTO));
    }

}
