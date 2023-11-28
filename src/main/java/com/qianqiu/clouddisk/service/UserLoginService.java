package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.model.dto.LoginDTO;
import com.qianqiu.clouddisk.model.dto.ReSetPwdDTO;
import com.qianqiu.clouddisk.model.dto.RegisterDTO;
import com.qianqiu.clouddisk.model.dto.SendEmailDTO;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import jakarta.servlet.http.HttpServletResponse;

public interface UserLoginService {
    void sendCode(HttpServletResponse response, String email, Integer type);

    int UserRegister(RegisterDTO registerDTO);

    void sendEmailCode(SendEmailDTO sendEmailDTO);

    UserInfoVo login(LoginDTO loginDTO);

    void reSetPassword(ReSetPwdDTO reSetPwdDTO);
}
