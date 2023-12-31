package com.magicrepokit.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.constant.ChatResultCode;
import com.magicrepokit.chat.constant.StatusConstant;
import com.magicrepokit.chat.dto.GptTokenGetDTO;
import com.magicrepokit.chat.entity.Knowledge;
import com.magicrepokit.chat.entity.UserGpt;
import com.magicrepokit.chat.mapper.KnowledgeMapper;
import com.magicrepokit.chat.mapper.UserGptMapper;
import com.magicrepokit.chat.service.IKnowledgeService;
import com.magicrepokit.chat.service.IUserGptService;
import com.magicrepokit.common.api.ResultCode;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mb.base.BaseServiceImpl;
import com.magicrepokit.oss.OssTemplate;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
public class KnowledgeServiceImpl extends BaseServiceImpl<KnowledgeMapper, Knowledge> implements IKnowledgeService {
    private OssTemplate ossTemplate;

    @Override
    public boolean uploadFileSplit(MultipartFile file) {
        //1.获取文件类似
        String fileName = file.getOriginalFilename();
        if(ObjectUtil.isEmpty(fileName)){
            throw new ServiceException(ChatResultCode.FILE_NAME_IS_NULL);
        }
        //2.获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        //3.判断是否txt,md,pdf
        if(!"txt".equals(suffix) && !"md".equals(suffix) && !"pdf".equals(suffix)){
            throw new ServiceException(ChatResultCode.FILE_TYPE_ERROR);
        }
        //4.判断文件大小
        if(file.getSize() > (1024L * 1024*1024 * 60)){
            throw new ServiceException(ChatResultCode.FILE_SIZE_ERROR);
        }
        ossTemplate.putFile(file);

//        InputStream inputStream = null;
//        try {
//            inputStream = file.getInputStream();
//        } catch (IOException e) {
//            throw new ServiceException(ChatResultCode.FILE_UPLOAD_ERROR);
//        }
//        if(inputStream == null){
//            return false;
//        }
//        //6.选择文档加载器
//        switch (suffix){
//            case "txt":
//
//                break;
//            case "md":
//                break;
//            case "pdf":
//                ApachePdfBoxDocumentParser apachePdfBoxDocumentParser = new ApachePdfBoxDocumentParser();
//                Document parse = apachePdfBoxDocumentParser.parse(inputStream);
//                System.out.println(parse);
//                break;
//            default:
//                break;
//        }

        return false;
    }
}
