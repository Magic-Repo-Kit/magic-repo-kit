package com.magicrepokit.chat.controller;

import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.common.api.R;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

}
