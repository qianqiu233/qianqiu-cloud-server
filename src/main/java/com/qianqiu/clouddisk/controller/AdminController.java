package com.qianqiu.clouddisk.controller;

import com.qianqiu.clouddisk.annotation.EnableCheck;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.service.AdminService;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Tag(name = "AdminController", description = "管理员操作相关")
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;


    @Operation(summary = "分页展示所有用户文件", description = "管理员")
    @PostMapping("/getAdminFileInfoList")
    @EnableCheck
    public CommonResult<CommonPage<AdminFileInfoDTO>> getAdminFileInfoList(@RequestBody FileInfoDTO fileInfoDTO) {
        return adminService.getAdminFileInfoList(fileInfoDTO);
    }
    @Operation(summary = "获取预览视频文件url", description = "管理员")
    @GetMapping("/getVideoUrl")
    public CommonResult getVideoUrl(@RequestParam("fileId") String fileId,
                                    @RequestParam("userId") String userId){
        return adminService.getVideoUrl(fileId,userId);
    }
    @Operation(summary = "获取预览音频文件url", description = "管理员")
    @GetMapping("/getAudioUrl")
    public CommonResult getAudioUrl(@RequestParam("fileId") String fileId,
                                    @RequestParam("userId") String userId){
        return adminService.getAudioUrl(fileId,userId);
    }
    @Operation(summary = "获取预览图片文件url", description = "管理员")
    @GetMapping("/getImage")
    public CommonResult getImage(@RequestParam("fileId") String fileId,
                                    @RequestParam("userId") String userId){
        return adminService.getImage(fileId,userId);
    }
    @Operation(summary = "获取预览blob", description = "管理员")
    @GetMapping("/getFile")
    public CommonResult getFile(@RequestParam("fileId") String fileId,
                                @RequestParam("userId") String userId,
                                HttpServletResponse response) {
        return adminService.getFile(fileId,userId,response);
    }
    @Operation(summary = "删除单个文件", description = "管理员")
    @PostMapping("/delAdminFileById")
    public CommonResult delAdminFileById(@RequestBody AdminIdInfoDTO adminIdInfoDTO) {
        return adminService.delAdminFileById(adminIdInfoDTO);
    }
    @Operation(summary = "删除多个文件", description = "管理员")
    @PostMapping("/delAdminFileList")
    public CommonResult delAdminFileList(@RequestBody List<AdminIdInfoDTO> listParams) {
        return adminService.delAdminFileList(listParams);
    }
    @Operation(summary = "获取用户列表", description = "管理员")
    @GetMapping("/getUserList")
    public CommonResult<CommonPage<AdminUserListDTO>> getUserList(
            @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value ="pageSize",defaultValue = "15") Integer pageSize,
            @RequestParam(value = "nickNameFuzzy",defaultValue = "") String nickNameFuzzy,
            Integer status){
        return adminService.getUserList(pageNum,pageSize,nickNameFuzzy,status);
    }
    @Operation(summary = "更改用户状态", description = "管理员")
    @PostMapping("/updateUserStatus")
    @EnableCheck
    public CommonResult updateUserStatus(@RequestBody UpdateUserStatusDTO updateUserStatusDTO){
        return adminService.updateUserStatus(updateUserStatusDTO);
    }
    @Operation(summary = "更改空间状态", description = "管理员")
    @PostMapping("/updateUserSpace")
    @EnableCheck
    public CommonResult updateUserSpace(@RequestBody UpdateUserSpaceDTO updateUserSpaceDTO){
        return adminService.updateUserSpace(updateUserSpaceDTO);
    }
    @Operation(summary = "创建文件的下载链接", description = "管理员")
    @GetMapping("/createDownloadToken")
    public CommonResult createDownloadToken(@RequestParam("fileId") String fileId,
                                            @RequestParam("userId") String userId){
        return adminService.createDownloadToken(fileId,userId);
    }
    @Operation(summary = "下载文件", description = "管理员")
    @GetMapping("/download")
    public CommonResult download(@RequestParam("dowToken") String dowToken){
//        todo 还需修改
        return adminService.download(dowToken);
    }
    @Operation(summary = "查看配置", description = "管理员")
    @GetMapping("/getSysSettings")
    public CommonResult getSysSettings(){
        return adminService.getSysSettings();
    }
    @Operation(summary = "修改配置", description = "管理员")
    @PostMapping("/updateSysSettings")
    public CommonResult updateSysSettings(@RequestBody SysSettingDTO sysSettingDTO){
        return adminService.updateSysSettings(sysSettingDTO);
    }

}
