package com.gpt.system.controller;

import com.gpt.common.api.R;
import com.gpt.oss.OssTemplate;
import com.gpt.oss.model.GPTFile;
import com.gpt.oss.model.OssFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对象存储接口
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("oss")
@AllArgsConstructor
@Api(value = "对象存储接口", tags = "对象存储")
public class OssController {
    private OssTemplate ossTemplate;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @ApiOperation(value = "上传文件")
    public R<GPTFile> uploadFile(@RequestParam("file") MultipartFile file){
        GPTFile GPTFile = ossTemplate.putFile(file);
        return R.data(GPTFile);
    }

    /**
     * 下载文件
     * @param fileName
     * @return
     */
    @GetMapping("/download")
    @ApiOperation(value = "下载文件")
    public R<OssFile> downloadFile(@RequestParam("fileName") String fileName){
        OssFile ossFile = ossTemplate.statFile(fileName);
        return R.data(ossFile);
    }
}
