package com.li.chat.controller;

import com.li.chat.common.utils.ResultData;
import com.li.chat.config.MinioConfig;
import com.li.chat.utils.MinioUtil;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author malaka
 */
@Slf4j
@RestController
@RequestMapping(value = "/chat-base/file")
public class FileController {


    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private MinioConfig prop;

    /**
     * 查看存储bucket是否存在
     * @param bucketName
     * @return
     */
    @GetMapping("/bucketExists")
    public ResultData bucketExists(@RequestParam("bucketName") String bucketName) {
        return ResultData.success().put("bucketName",minioUtil.bucketExists(bucketName));
    }

    /**
     * 创建存储bucket
     * @param bucketName
     * @return
     */
    @GetMapping("/makeBucket")
    public ResultData makeBucket(@RequestParam("bucketName") String bucketName) {
        return ResultData.success().put("bucketName",minioUtil.makeBucket(bucketName));
    }

    /**
     * 删除存储bucket
     * @param bucketName
     * @return
     */
    @GetMapping("/removeBucket")
    public ResultData removeBucket(@RequestParam("bucketName") String bucketName) {
        return ResultData.success().put("bucketName",minioUtil.removeBucket(bucketName));
    }

    /**
     * 获取全部bucket
     * @return
     */
    @GetMapping("/getAllBuckets")
    public ResultData getAllBuckets() {
        List<Bucket> allBuckets = minioUtil.getAllBuckets();
        return ResultData.success().put("allBuckets",allBuckets);
    }

    /**
     * 文件上传返回url
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public String upload(@RequestPart("file") MultipartFile file, @RequestParam("module") String module) {
        String objectName = minioUtil.upload(file, module);
        if (null != objectName) {
            return prop.getEndpoint() + "/" + prop.getBucketName() + "/" + objectName;
        }
        return null;
    }

    /**
     * 文件预览
     * @param fileName
     * @return
     */
    @GetMapping("/preview")
    public ResultData preview(@RequestParam("fileName") String fileName) {
        return ResultData.success().put("fileName",minioUtil.preview(fileName));
    }

    /**
     * 文件下载
     * @param fileName
     * @param res
     * @return
     */
    @GetMapping("/download")
    public ResultData download(@RequestParam("fileName") String fileName, HttpServletResponse res) {
        minioUtil.download(fileName,res);
        return ResultData.success();
    }

    /**
     * 删除文件
     * @param url
     * @return
     */
    @PostMapping("/delete")
    public ResultData remove(@RequestParam("url") String url) {
        String objName = url.substring(url.lastIndexOf(prop.getBucketName()+"/") + prop.getBucketName().length()+1);
        System.out.println(objName);
        minioUtil.remove(objName);
        return ResultData.success().put("objName",objName);
    }

}