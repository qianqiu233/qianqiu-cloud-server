package com.qianqiu.clouddisk.controller;

import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.service.FileRecycleService;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "FileRecycleController", description = "文件回收站相关")
@RequestMapping("/recycle")
public class FileRecycleController {
    @Autowired
    private FileRecycleService fileRecycleService;
    @Operation(summary = "获取回收站文件列表", description = "回收站")
    @GetMapping("/getRecycleFileList")
    public CommonResult<CommonPage<FileInfo>> getRecycleFileList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value ="pageSize",defaultValue = "15") Integer pageSize){
        return fileRecycleService.getRecycleFileList(pageNum,pageSize);
    }
    @Operation(summary = "彻底删除回收站文件", description = "回收站")
    @PostMapping("/delRecycleFileById")
    public CommonResult delRecycleFileById(String fileId){
        return fileRecycleService.delRecycleFileById(fileId);
    }
    @Operation(summary = "批量彻底删除回收站文件", description = "回收站")
    @PostMapping("/delRecycleFileList")
    public CommonResult delRecycleFileList(@RequestBody List<String> fileIds){
        return fileRecycleService.delRecycleFileList(fileIds);
    }
    @Operation(summary = "还原回收站文件", description = "回收站")
    @PostMapping("/recoverFileById")
    public CommonResult recoverFileById(String fileId){
        return fileRecycleService.recoverFileById(fileId);
    }
    @Operation(summary = "批量还原回收站文件", description = "回收站")
    @PostMapping("/recoverFileList")
    public CommonResult recoverFileList(@RequestBody List<String> fileIds){
        return fileRecycleService.recoverFileList(fileIds);
    }

}
