package com.magicrepokit.system.controller;

import com.magicrepokit.common.api.R;
import com.magicrepokit.oss.OssTemplate;
import com.magicrepokit.oss.model.MRKFile;
import com.magicrepokit.oss.model.OssFile;
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
    public R<MRKFile> uploadFile(@RequestParam("file") MultipartFile file){
        MRKFile mrkFile = ossTemplate.putFile(file);
        return R.data(mrkFile);
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
