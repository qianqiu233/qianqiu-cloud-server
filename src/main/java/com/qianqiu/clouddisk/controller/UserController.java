package com.qianqiu.clouddisk.controller;

import com.qianqiu.clouddisk.annotation.EnableCheck;
import com.qianqiu.clouddisk.model.dto.UpdateUserPwdDTO;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.service.UserService;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Tag(name = "UserController", description = "用户操作相关")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 进入界面获取头像
     * @return
     */
    @Operation(summary = "展示头像", description = "头像")
    @GetMapping("/getAvatar")
    @EnableCheck
    public CommonResult getAvatar(){
        return userService.getAvatar();
    }
    @Operation(summary = "修改头像", description = "头像")
    @PostMapping("/updateUserAvatar")
    @EnableCheck
    public CommonResult updateUserAvatar(@RequestPart MultipartFile avatar) throws IOException {
        return userService.updateUserAvatar(avatar);
    }
    @Operation(summary = "修改用户密码", description = "修改密码")
    @PostMapping("/updateUserPassword")
    @EnableCheck
    public CommonResult updateUserPassword(@RequestBody UpdateUserPwdDTO updateUserPwdDTO){
        return userService.updateUserPassword(updateUserPwdDTO);
    }
    @Operation(summary = "退出登录", description = "退出登录")
    @GetMapping("/userLogout")
    @EnableCheck
    public CommonResult userLogout(HttpServletRequest request){
        return userService.userLogout(request);
    }
    @Operation(summary = "查询用户信息", description = "查询用户")
    @GetMapping("/selectUserInfo")
    public CommonResult selectUserInfo(String userId){
        UserInfoVo userInfoVo = userService.selectUserInfo(userId);
        return CommonResult.success(userInfoVo);
    }
    @Operation(summary = "获取用户使用空间", description = "用户空间")
    @GetMapping("/getUserSpace")
    public CommonResult getUseSpace(){
        return userService.getUseSpace();
    }

}
