package com.qianqiu.clouddisk.controller;

import com.qianqiu.clouddisk.annotation.EnableCheck;
import com.qianqiu.clouddisk.annotation.ParamCheck;
import com.qianqiu.clouddisk.model.dto.LoginDTO;
import com.qianqiu.clouddisk.model.dto.ReSetPwdDTO;
import com.qianqiu.clouddisk.model.dto.RegisterDTO;
import com.qianqiu.clouddisk.model.dto.SendEmailDTO;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.service.UserLoginService;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import com.qianqiu.clouddisk.utils.enums.RegexEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "UserLoginController", description = "用户登录相关")
@RestController
@RequestMapping("/userLogin")
public class UserLoginController {
    @Autowired
    private UserLoginService userLoginService;

    /**
     * 获取验证码
     *
     * @param response
     */
    @Operation(summary = "发送图片或验证码", description = "验证码相关")
    @GetMapping("/sendCode")
    @EnableCheck
    public void sendCode(HttpServletResponse response, String key, Integer type) {
        userLoginService.sendCode(response, key, type);
    }

    /**
     * 邮箱注册
     *
     * @param registerDTO
     * @return
     */
    @Operation(summary = "邮箱注册", description = "注册")
    @PostMapping("/register")
    @EnableCheck
    public CommonResult UserRegister(@RequestBody RegisterDTO registerDTO) {

        return userLoginService.UserRegister(registerDTO);

    }

    /**
     * 发送邮箱验证码
     *
     * @param
     * @return
     */
    @Operation(summary = "发送邮箱验证码", description = "验证码相关")
    @PostMapping("/sendEmailCode")
    @EnableCheck
    public CommonResult sendEmailCode(@RequestBody SendEmailDTO sendEmailDTO) {
        userLoginService.sendEmailCode(sendEmailDTO);
        return CommonResult.success(null,"邮箱验证码成功发送");
    }

    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    @Operation(summary = "用户登录", description = "登录")
    @PostMapping("/login")
    @EnableCheck
    public CommonResult Login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        return userLoginService.login(loginDTO,session);
    }

    /**
     * 重置密码
     * @param reSetPwdDTO
     * @return
     */
    @Operation(summary = "重置密码", description = "重置密码")
    @PostMapping("/resetPwd")
    @EnableCheck
    public CommonResult reSetPassword(@RequestBody ReSetPwdDTO reSetPwdDTO){
        return userLoginService.reSetPassword(reSetPwdDTO);
    }
}
