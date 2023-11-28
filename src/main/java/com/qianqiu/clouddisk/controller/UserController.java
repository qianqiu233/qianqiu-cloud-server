package com.qianqiu.clouddisk.controller;

import com.qianqiu.clouddisk.annotation.EnableCheck;
import com.qianqiu.clouddisk.annotation.ParamCheck;
import com.qianqiu.clouddisk.model.dto.UpdateUserPwdDTO;
import com.qianqiu.clouddisk.service.UserService;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Tag(name = "UserController", description = "用户操作相关")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 进入界面获取头像
     * @param userId
     * @return
     */
    @Operation(summary = "展示头像", description = "头像")
    @GetMapping("/getAvatar/{userId}")
    @EnableCheck
    public CommonResult getAvatar(@PathVariable("userId") @ParamCheck String userId){
        return userService.getAvatar(userId);
    }
    @Operation(summary = "修改头像", description = "头像")
    @PostMapping("/updateUserAvatar")
    @EnableCheck
    public CommonResult updateUserAvatar(@RequestPart MultipartFile avatar,@ParamCheck String userId){
        System.out.println(userId);
        return userService.updateUserAvatar(avatar,userId);
    }
    @Operation(summary = "修改用户密码", description = "修改密码")
    @PostMapping("/updateUserPassword")
    @EnableCheck
    public CommonResult updateUserPassword(@RequestBody UpdateUserPwdDTO updateUserPwdDTO){
        return userService.updateUserPassword(updateUserPwdDTO);
    }
}
