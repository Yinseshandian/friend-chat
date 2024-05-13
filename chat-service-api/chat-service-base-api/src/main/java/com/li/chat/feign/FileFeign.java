package com.li.chat.feign;

import com.li.chat.common.utils.ResultData;
import com.li.chat.config.FeignMultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author malaka
 */
@FeignClient(name = "chat-base",
        contextId = "file",
        configuration = FeignMultipartSupportConfig.class)
@RequestMapping("/file")
public interface FileFeign {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(@RequestPart("file") MultipartFile file, @RequestParam("module") String module);

}
