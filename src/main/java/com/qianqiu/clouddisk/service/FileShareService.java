package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.model.dto.FileShareDTO;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;

import java.util.List;

public interface FileShareService {
    CommonResult fileShare(String fileId, Integer validType, String code, Integer codeType, String webAddr);

    CommonResult<CommonPage<FileShareDTO>> getShareFileList(Integer pageNum, Integer pageSize);

    CommonResult cancelShare(List<String> shareIds);
}
