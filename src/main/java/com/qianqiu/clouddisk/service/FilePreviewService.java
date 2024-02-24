package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface FilePreviewService{
    CommonResult getVideoUrl(String fileId);

    CommonResult getFile(String fileId, HttpServletResponse response);

    CommonResult getAudioUrl(String fileId);

    CommonResult getImage(String fileId);
}
