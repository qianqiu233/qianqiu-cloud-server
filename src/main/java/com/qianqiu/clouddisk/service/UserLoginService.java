package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.model.dto.LoginDTO;
import com.qianqiu.clouddisk.model.dto.ReSetPwdDTO;
import com.qianqiu.clouddisk.model.dto.RegisterDTO;
import com.qianqiu.clouddisk.model.dto.SendEmailDTO;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import jakarta.servlet.http.HttpServletResponse;

public interface UserLoginService {
    void sendCode(HttpServletResponse response, String key, Integer type);

    CommonResult UserRegister(RegisterDTO registerDTO);

    void sendEmailCode(SendEmailDTO sendEmailDTO);

    CommonResult login(LoginDTO loginDTO);

    CommonResult reSetPassword(ReSetPwdDTO reSetPwdDTO);
}
