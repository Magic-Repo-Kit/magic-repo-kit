package com.magicrepokit.chat.controller;

import com.magicrepokit.chat.dto.knowledge.*;
import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.chat.vo.*;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
     * 建立文件夹(文件)
     */
    @PostMapping("/create")
    @ApiOperation(value = "建立文件夹(文件)")
    public R<KnowledgeVO> create(@RequestBody KnowledgeCreateDTO createDTO){
        return R.data(knowledgeService.create(createDTO));
    }

    /**
     * 文件夹(文件)列表分页
     */
    @GetMapping("/list-page")
    @ApiOperation(value = "文件夹(文件)列表分页")
    public R<PageResult<KnowledgeListVO>> page(KnowledgeListDTO knowledgeListDTO){
        return R.data(knowledgeService.page(knowledgeListDTO));
    }

    /**
     * 知识库文件详情
     */
    @GetMapping("/detail-file/{id}")
    @ApiOperation(value = "知识库文件详情")
    public R<KnowledgeFileListVO> detailFile(@PathVariable("id") Long id){
        return R.data(knowledgeService.detailFile(id));
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
     * 移动文件夹(文件)
     */
    @PostMapping("/move")
    @ApiOperation(value = "移动文件夹(文件)")
    public R<Boolean> move(@RequestBody KnowledgeMoveDTO knowledgeMoveDTO){
        return R.status(knowledgeService.move(knowledgeMoveDTO));
    }

    /**
     * 删除文件夹(文件)
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除移动文件夹(文件)")
    public R<Boolean> delete(String knowledgeIds){
        return R.status(knowledgeService.delete(knowledgeIds));
    }

    /**
     * 文件内容上传处理(单文件处理)
     */
    @PostMapping("/process")
    @ApiOperation(value = "文件内容上传处理")
    public R<Boolean> process(@RequestBody KnowledgeProcessDTO knowledgeProcessDTO){
        return R.status(knowledgeService.process(knowledgeProcessDTO));
    }

    /**
     * 文件内容上传处理(多文件处理)
     */
    @PostMapping("/process-batch")
    @ApiOperation(value = "文件内容上传处理")
    public R<Boolean> processBatch(@RequestBody KnowledgeProcessBatchDTO knowledgeProcessBatchDTO){
        return R.status(knowledgeService.processBatch(knowledgeProcessBatchDTO));
    }

    /**
     * 文件内容处理状态详情列表
     */
    @GetMapping("/list-file-status")
    @ApiOperation(value = "文件处理状态详情列表")
    public R<List<KnowledgeFileVO>> listFileStatus(){
        return R.data(knowledgeService.listFileStatus());
    }

    /**
     * 文件重新处理
     */
    @PostMapping("/reprocess/{knowledgeDetailId}")
    @ApiOperation(value = "文件重新处理")
    public R<Boolean> reprocess(@PathVariable("knowledgeDetailId") Long knowledgeDetailId){
        return R.status(knowledgeService.reprocess(knowledgeDetailId));
    }

    /**
     * 文件内容列表
     */
    @GetMapping("/list-file/{knowledgeId}")
    @ApiOperation(value = "文件内容列表")
    public R<List<KnowledgeFileVO>> listFile(@PathVariable("knowledgeId") Long knowledgeId){
        return R.data(knowledgeService.listFile(knowledgeId));
    }

    /**
     * 文件内容删除
     */
    @DeleteMapping("/delete-file/")
    @ApiOperation(value = "文件内容删除")
    public R<Boolean> deleteFile(String knowledgeIds){
        return R.status(knowledgeService.deleteFile(knowledgeIds));
    }
}
