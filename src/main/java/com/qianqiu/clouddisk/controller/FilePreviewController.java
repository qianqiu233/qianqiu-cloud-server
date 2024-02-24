package com.qianqiu.clouddisk.controller;

import com.qianqiu.clouddisk.service.FilePreviewService;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@Tag(name = "FilePreviewController", description = "文件预览相关")
@RequestMapping("/preview")
public class FilePreviewController {
    @Autowired
    private FilePreviewService filePreviewService;
    @Operation(summary = "获取预览视频文件url", description = "预览")
    @GetMapping("/getVideoUrl/{fileId}")
    public CommonResult getVideoUrl(@PathVariable("fileId") String fileId){
        return filePreviewService.getVideoUrl(fileId);
    }
    @Operation(summary = "获取预览音频文件url", description = "预览")
    @GetMapping("/getAudioUrl/{fileId}")
    public CommonResult getAudioUrl(@PathVariable("fileId") String fileId){
        return filePreviewService.getAudioUrl(fileId);
    }
    @Operation(summary = "获取预览图片url", description = "预览")
    @GetMapping("/getImage/{fileId}")
    public CommonResult getImage(@PathVariable("fileId") String fileId){
        System.out.println(fileId);
        return filePreviewService.getImage(fileId);
    }
    @Operation(summary = "获取预览blob", description = "预览")
    @GetMapping("/getFile/{fileId}")
    public CommonResult getFile(@PathVariable("fileId") String fileId, HttpServletResponse response) {
        return filePreviewService.getFile(fileId,response);
    }

}
