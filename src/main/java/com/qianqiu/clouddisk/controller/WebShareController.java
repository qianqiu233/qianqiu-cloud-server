package com.qianqiu.clouddisk.controller;

import com.qianqiu.clouddisk.annotation.EnableCheck;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.service.WebShareService;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/showShare")
public class WebShareController {
    @Autowired
    private WebShareService webShareService;
    @Operation(summary = "判断浏览器缓存是否存在用户信息", description = "外部分享")
    @GetMapping("/getShareLoginInfo/{webShareId}")
    public CommonResult getShareLoginInfo(@PathVariable("webShareId") String webShareId) throws UnsupportedEncodingException {
        return webShareService.getShareLoginInfo(webShareId);
    }
    @Operation(summary = "获取分享文件信息", description = "外部分享")
    @GetMapping("/getShareInfo/{shareId}")
    public CommonResult getShareInfo(@PathVariable("shareId") String shareId){
        return webShareService.getShareInfo(shareId);
    }
    @Operation(summary = "校验分享码", description = "外部分享")
    @PostMapping("/checkShareCode")
    @EnableCheck
    public CommonResult checkShareCode(@RequestBody CheckShareCodeDTO checkShareCodeDTO,HttpSession session){
        return webShareService.checkShareCode(checkShareCodeDTO,session);
    }
    @Operation(summary = "分页查询当前分享的文件", description = "外部分享")
    @PostMapping("/selectSharedFile")
    @EnableCheck
    public CommonResult<CommonPage<WebFileInfoDTO>> selectSharedFile(@RequestBody SelectSharedFileDTO selectSharedFileDTO){
        return webShareService.selectSharedFile(selectSharedFileDTO);
    }
    @Operation(summary = "获取目录信息", description = "外部分享")
    @PostMapping("/getFolderInfo")
    public CommonResult getFolderInfo(@RequestBody GetFolderInfoDTO getFolderInfoDTO ){
        System.out.println(getFolderInfoDTO);
        return webShareService.getFolderInfo(getFolderInfoDTO);
    }
    @Operation(summary = "获取预览视频文件url", description = "外部分享")
    @GetMapping("/getVideoUrl")
    public CommonResult getVideoUrl(@RequestParam("fileId") String fileId,
                                    @RequestParam("shareId") String shareId,
                                    @RequestParam("userId") String userId){
        return webShareService.getVideoUrl(fileId,shareId,userId);
    }
    @Operation(summary = "获取预览音频文件url", description = "外部分享")
    @GetMapping("/getAudioUrl")
    public CommonResult getAudioUrl(@RequestParam("fileId") String fileId,
                                    @RequestParam("shareId") String shareId,
                                    @RequestParam("userId") String userId){
        return webShareService.getAudioUrl(fileId,shareId,userId);
    }
    @Operation(summary = "获取预览图片文件url", description = "外部分享")
    @GetMapping("/getImage")
    public CommonResult getImage(@RequestParam("fileId") String fileId,
                                 @RequestParam("shareId") String shareId,
                                 @RequestParam("userId") String userId){
        return webShareService.getImage(fileId,shareId,userId);
    }
    @Operation(summary = "获取预览blob", description = "外部分享")
    @GetMapping("/getFile")
    public CommonResult getFile(@RequestParam("fileId") String fileId,
                                @RequestParam("shareId") String shareId,
                                @RequestParam("userId") String userId,
                                HttpServletResponse response) {
        return webShareService.getFile(fileId,shareId,userId,response);
    }
    @Operation(summary = "创建文件的下载链接", description = "外部分享")
    @GetMapping("/createDownloadToken")
    public CommonResult createDownloadToken(@RequestParam("fileId") String fileId,
                                            @RequestParam("shareId") String shareId,
                                            @RequestParam("userId") String userId){
        return webShareService.createDownloadToken(fileId,userId,shareId);
    }
    @Operation(summary = "下载文件", description = "外部分享")
    @GetMapping("/download")
    public CommonResult download(@RequestParam("dowToken") String dowToken){
        return webShareService.download(dowToken);
    }
    @Operation(summary = "下载文件", description = "外部分享")
    @PostMapping("/saveWebShare")
    public CommonResult saveWebShare(@RequestBody SaveWebShareDTO saveWebShareDTO){
        return webShareService.saveWebShare(saveWebShareDTO);
    }

}
