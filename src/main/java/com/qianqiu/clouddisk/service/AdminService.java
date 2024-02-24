package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface AdminService {
    CommonResult<CommonPage<AdminFileInfoDTO>> getAdminFileInfoList(FileInfoDTO fileInfoDTO);

    CommonResult getVideoUrl(String fileId, String userId);

    CommonResult getAudioUrl(String fileId, String userId);

    CommonResult getFile(String fileId, String userId, HttpServletResponse response);

    CommonResult delAdminFileById(AdminIdInfoDTO adminIdInfoDTO);

    CommonResult delAdminFileList(List<AdminIdInfoDTO> listParams);

    CommonResult<CommonPage<AdminUserListDTO>> getUserList(Integer pageNum, Integer pageSize, String nickNameFuzzy, Integer status);

    CommonResult getImage(String fileId, String userId);

    CommonResult updateUserStatus(UpdateUserStatusDTO updateUserStatusDTO);

    CommonResult updateUserSpace(UpdateUserSpaceDTO updateUserSpaceDTO);

    CommonResult createDownloadToken(String fileId, String userId);

    CommonResult download(String dowToken);

    CommonResult getSysSettings();

    CommonResult updateSysSettings(SysSettingDTO sysSettingDTO);
}
