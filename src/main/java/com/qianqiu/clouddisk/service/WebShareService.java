package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.UnsupportedEncodingException;

public interface WebShareService {
    CommonResult getShareLoginInfo( String webShareId) throws UnsupportedEncodingException;

    CommonResult getShareInfo(String shareId);

    CommonResult checkShareCode(CheckShareCodeDTO checkShareCodeDTO, HttpSession session);

    CommonResult<CommonPage<WebFileInfoDTO>> selectSharedFile(SelectSharedFileDTO selectSharedFileDTO);

    CommonResult getFolderInfo(GetFolderInfoDTO getFolderInfoDTO);

    CommonResult getVideoUrl(String fileId, String shareId, String userId);

    CommonResult getAudioUrl(String fileId, String shareId, String userId);

    CommonResult getImage(String fileId, String shareId, String userId);

    CommonResult getFile(String fileId, String shareId, String userId, HttpServletResponse response);

    CommonResult createDownloadToken(String fileId, String userId, String shareId);

    CommonResult download(String dowToken);

    CommonResult saveWebShare(SaveWebShareDTO saveWebShareDTO);
}
