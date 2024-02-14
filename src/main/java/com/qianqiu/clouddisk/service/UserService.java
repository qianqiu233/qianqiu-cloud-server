package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.model.dto.UpdateUserPwdDTO;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    CommonResult getAvatar();

    CommonResult updateUserAvatar(MultipartFile file) throws IOException;
    CommonResult updateUserPassword(UpdateUserPwdDTO updateUserPwdDTO);

    CommonResult userLogout(HttpServletRequest request);

    UserInfoVo selectUserInfo(String userId);

    CommonResult getUseSpace();
    Boolean UpdateUserSpace(List<FileInfo> fileInfoList, int IncOrDec);

}
