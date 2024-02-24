package com.qianqiu.clouddisk.controller;

import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.mbg.mbg_model.FileShare;
import com.qianqiu.clouddisk.model.dto.FileShareDTO;
import com.qianqiu.clouddisk.service.FileShareService;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "FileShareController", description = "文件分享相关")
@RequestMapping("/share")
public class FileShareController {
    @Autowired
    private FileShareService fileShareService;
    @Operation(summary = "分享文件", description = "分享")
    @GetMapping("/fileShare")
    public CommonResult fileShare(@RequestParam("fileId") String fileId,
                                  @RequestParam("validType") Integer validType,
                                  @RequestParam("code") String code,
                                  @RequestParam("codeType") Integer codeType,
                                  @RequestParam("webAddr") String webAddr){
        return fileShareService.fileShare(fileId,validType,code,codeType,webAddr);
    }
    @Operation(summary = "获取分享文件列表", description = "分享")
    @GetMapping("/getShareFileList")
    public CommonResult<CommonPage<FileShareDTO>> getShareFileList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                                   @RequestParam(value ="pageSize",defaultValue = "15") Integer pageSize){
        return fileShareService.getShareFileList(pageNum,pageSize);
    }
    @Operation(summary = "获取分享文件列表", description = "分享")
    @PostMapping("/cancelShare")
    public CommonResult cancelShare(@RequestBody List<String> shareIds){
        return fileShareService.cancelShare(shareIds);
    }

}
