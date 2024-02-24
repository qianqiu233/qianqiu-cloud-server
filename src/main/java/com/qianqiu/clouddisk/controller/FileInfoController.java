package com.qianqiu.clouddisk.controller;

import com.qianqiu.clouddisk.annotation.EnableCheck;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.service.FileInfoService;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "FileInfoController", description = "文件操作相关")
@RequestMapping("/file")
public class FileInfoController {
    @Autowired
    private FileInfoService fileInfoService;

    @Operation(summary = "分页展示所有文件", description = "文件")
    @PostMapping("/getFileInfoList")
    @EnableCheck
    public CommonResult<CommonPage<FileInfo>> getFileInfoList(@RequestBody FileInfoDTO fileInfoDTO) {
        return fileInfoService.getFileInfoList(fileInfoDTO);
    }

    @Operation(summary = "上传文件", description = "文件")
    @PostMapping("/uploadFile")
    @EnableCheck
    public CommonResult uploadFile(@RequestParam("fileName") String fileName,
                                   @RequestParam("fileMd5") String fileMd5,
                                   @RequestParam("chunkIndex") Integer chunkIndex,
                                   @RequestParam("chunks") Integer chunks,
                                   @RequestParam("fileId") String fileId,
                                   @RequestParam("filePid") String filePid,
                                   @RequestParam("sourceFileSize") Long sourceFileSize,
                                   @RequestParam("sourceFileType") String sourceFileType,
                                   @RequestPart("file") MultipartFile file) {
        return fileInfoService.uploadFile(fileName, fileMd5, chunkIndex, chunks, fileId, filePid, file, sourceFileSize, sourceFileType);
    }

    @Operation(summary = "创建文件夹", description = "文件")
    @PostMapping("/createNewFolder")
    @EnableCheck
    public CommonResult<FileInfo> createNewFolder(@RequestBody FolderDTO folderDTO) {
        return fileInfoService.createNewFolder(folderDTO);
    }

    @Operation(summary = "重命名文件", description = "文件")
    @PostMapping("/renameFile")
    @EnableCheck
    public CommonResult renameFile(@RequestBody RenameFileDTO renameFileDTO) {
        return fileInfoService.renameFile(renameFileDTO);
    }

    @GetMapping("/getCover/{imageFolder}/{imageName}")
    public CommonResult getCover(@PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName) {
        //TODO 或许没有用
        return null;
    }

    @Operation(summary = "批量删除文件", description = "文件")
    @PostMapping("/delFileListByIds")
    public CommonResult delFileListByIds(@RequestBody List<String> fileIds) {
        return fileInfoService.delFileListByIds(fileIds);
    }

    @Operation(summary = "删除单个文件", description = "文件")
    @PostMapping("/delFileById")
    public CommonResult delFileById(String fileId) {
        return fileInfoService.delFileById(fileId);
    }

    @Operation(summary = "查询所有目录", description = "文件")
    @EnableCheck
    @PostMapping("/selectAllFolder")
    public CommonResult selectAllFolder(@RequestBody SelectAllFolderDTO selectAllFolderDTO) {
        System.out.println(selectAllFolderDTO);
        return fileInfoService.selectAllFolder(selectAllFolderDTO);
    }

    @Operation(summary = "移动单个文件", description = "文件")
    @EnableCheck
    @PostMapping("/moveFileToFolderById")
    public CommonResult moveFileToFolderById(@RequestBody MoveFileToFolderByIdDTO moveFileToFolderByIdDTO) {
        return fileInfoService.moveFileToFolderById(moveFileToFolderByIdDTO);
    }
    @Operation(summary = "移动多个文件", description = "文件")
    @EnableCheck
    @PostMapping("/moveFileListToFolderByIds")
    public CommonResult moveFileListToFolderByIds(@RequestBody MoveFileListToFolderByIdsDTO moveFileListToFolderByIdsDTO) {
        return fileInfoService.moveFileListToFolderByIds(moveFileListToFolderByIdsDTO);
    }
    @Operation(summary = "获取目录信息", description = "文件")
    @PostMapping("/getFolderInfo")
    public CommonResult getFolderInfo(@RequestBody GetFolderInfoDTO getFolderInfoDTO ){
        System.out.println(getFolderInfoDTO);
        return fileInfoService.getFolderInfo(getFolderInfoDTO);
    }
    @Operation(summary = "创建文件的下载链接", description = "文件")
    @GetMapping("/createDownloadToken/{fileId}")
    public CommonResult createDownloadToken(@PathVariable("fileId") String fileId){
        return fileInfoService.createDownloadToken(fileId);
    }
    @Operation(summary = "下载文件", description = "文件")
    @GetMapping("/download")
    public CommonResult download(@RequestParam("dowToken") String dowToken){
//        todo 还需修改
        return fileInfoService.download(dowToken);
    }

}
