package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/*
* 通用接口，用于文件的上传
*
* */
@RestController
@Slf4j
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {
    //
    @Value("${file.upload-dir}")
    private String uploadDir;


    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    //参数名必须和前端传过来的一致
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
        try {
            // uuid + 文件后缀
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;
            //这个就是文件路径,这里上传到本地的，就不要这个了
//            String filePath = aliOssUtil.upload(file.getBytes(), objectName);

            // 保存文件到服务器
            File destFile = new File(uploadDir, objectName);
            file.transferTo(destFile);
            String url = "http://localhost:8080/files/" + objectName;
            return Result.success(url);

        } catch (IOException e) {
            log.error("文件上传失败：{}",e);

        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
