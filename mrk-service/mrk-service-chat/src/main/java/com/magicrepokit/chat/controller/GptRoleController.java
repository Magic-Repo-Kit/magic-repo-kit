package com.magicrepokit.chat.controller;

import com.magicrepokit.chat.dto.gptRole.GptRoleCreateDTO;
import com.magicrepokit.chat.dto.gptRole.GptRolePageDTO;
import com.magicrepokit.chat.dto.gptRole.GptRoleUpdateDTO;
import com.magicrepokit.chat.service.IGptRoleService;
import com.magicrepokit.chat.vo.gptRole.GptRolePageVO;
import com.magicrepokit.chat.vo.gptRole.GptRoleVO;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.common.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * GPT角色管理接口
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("role")
@AllArgsConstructor
@Api(value = "GPT角色管理接口", tags = "GPT角色管理接口")
public class GptRoleController {
    private final IGptRoleService gptRoleService;

    /**
     * Gpt角色列表分页
     */
    @GetMapping("/list-page")
    @ApiOperation(value = "Gpt角色列表分页")
    public R<PageResult<GptRolePageVO>> page(GptRolePageDTO pageDTO){
        return R.data(gptRoleService.page(pageDTO));
    }


    /**
     * 建立Gpt角色
     */
    @PostMapping("/create")
    @ApiOperation(value = "建立Gpt角色")
    public R<GptRoleVO> create(@RequestBody GptRoleCreateDTO createDTO){
        return R.data(gptRoleService.create(createDTO));
    }

    /**
     * 修改Gpt角色
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改Gpt角色")
    public R<GptRoleVO> update(@RequestBody GptRoleUpdateDTO updateDTO){
        return R.data(gptRoleService.updateByDto(updateDTO));
    }

    /**
     * 删除Gpt角色
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除Gpt角色")
    public R<Boolean> delete(@PathVariable("id") Long id){
        return R.data(gptRoleService.deleteById(id));
    }

    /**
     * 根据id获取Gpt角色
     */
    @GetMapping("/detail/{id}")
    @ApiOperation(value = "根据id获取Gpt角色")
    public R<GptRoleVO> select(@PathVariable("id") Long id){
        return R.data(gptRoleService.detailById(id));
    }
}
