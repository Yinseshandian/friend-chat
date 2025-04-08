package com.li.chat.controller.common;

import com.google.common.base.Objects;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.common.utils.CheckImagesFormatUtil;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.feign.FileFeign;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author malaka
 */
@Api(tags = "0001文件接口")
@Slf4j
@RestController
@RequestMapping("/common/file")
public class FileController {


    private final FileFeign fileFeign;

    public FileController(FileFeign fileFeign) {
        this.fileFeign = fileFeign;
    }

    @ApiOperation("上传文件")
    @GlobalTransactional
    @PostMapping("/upload")
    public ResultData upload(@RequestPart("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        long size = file.getSize();
        log.info("common-file-用户 {} 上传文件：{} 大小:{}", RequestContext.getUserId(), originalFilename, size);
        String url = fileFeign.upload(file, "common-file");
        return ResultData.success()
                .put("fullPath", url)
                .put("fileName", originalFilename);
    }

    @ApiOperation("上传头像")
    @GlobalTransactional
    @PostMapping("/avatar")
    public ResultData avatar(@RequestPart("file") MultipartFile file) {
        String type = file.getContentType();
        type = type.substring(0, type.indexOf("/"));
        if (!Objects.equal("image",type)) {
            return ResultData.error(WebErrorCodeEnum.USER_INFO_AVATAR_FILE_TYPE_WRONG);
        }
        String originalFilename = file.getOriginalFilename();
        long size = file.getSize();
        log.info("admin-avatar-用户 {} 上传文件：{} 大小:{}",RequestContext.getUserId(), originalFilename, size);
        String url = fileFeign.upload(file, "admin-avatar");
        return ResultData.success().put("url", url);
    }

    @ApiOperation("上传用户头像")
    @GlobalTransactional
    @PostMapping("/useravatar")
    public ResultData useravatar(@RequestPart("file") MultipartFile file) {
        String type = file.getContentType();
        type = type.substring(0, type.indexOf("/"));
        if (!Objects.equal("image",type)) {
            return ResultData.error(WebErrorCodeEnum.USER_INFO_AVATAR_FILE_TYPE_WRONG);
        }
        String originalFilename = file.getOriginalFilename();
        long size = file.getSize();
        log.info("admin-avatar-用户 {} 上传文件：{} 大小:{}",RequestContext.getUserId(), originalFilename, size);
        String url = fileFeign.upload(file, "user-avatar");
        return ResultData.success().put("url", url);
    }
}
