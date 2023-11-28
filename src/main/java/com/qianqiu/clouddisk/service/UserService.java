package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.model.dto.UpdateUserPwdDTO;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    CommonResult getAvatar(String userId);

    CommonResult updateUserAvatar(MultipartFile file,String userId);
    CommonResult updateUserPassword(UpdateUserPwdDTO updateUserPwdDTO);
}
