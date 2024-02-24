package com.qianqiu.clouddisk.service;

import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;

import java.util.List;

public interface FileRecycleService {
    CommonResult<CommonPage<FileInfo>> getRecycleFileList(Integer pageNum, Integer pageSize);

    CommonResult delRecycleFileById(String fileId);

    CommonResult delRecycleFileList(List<String> fileIds);

    CommonResult recoverFileList(List<String> fileIds);

    CommonResult recoverFileById(String fileId);
}
