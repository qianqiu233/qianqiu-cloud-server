package com.qianqiu.clouddisk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.pagehelper.PageHelper;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileShareMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.mbg.mbg_model.FileShare;
import com.qianqiu.clouddisk.mbg.mbg_model.FileShareExample;
import com.qianqiu.clouddisk.model.dto.FileShareDTO;
import com.qianqiu.clouddisk.service.FileShareService;
import com.qianqiu.clouddisk.service.MinioService;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import com.qianqiu.clouddisk.utils.enums.ShareValidTypeEnums;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.*;
import static com.qianqiu.clouddisk.utils.FileAboutUtil.getShareValidType;


@Service
public class FileShareServiceImpl implements FileShareService {
    @Autowired
    private MinioService minioService;
    @Resource
    private FileShareMapper fileShareMapper;
    @Resource
    private FileInfoMapper fileInfoMapper;
    @Override
    public CommonResult fileShare(String fileId, Integer validType, String code, Integer codeType, String webAddr) {
        String userId = UserThreadLocal.getUserId();
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        if (fileInfo==null){
            throw new CommonException("文件出现错误");
        }
        String filePath = fileInfo.getFilePath();
        ShareValidTypeEnums shareValidType = getShareValidType(validType);
        String shareId = IdUtil.simpleUUID();
        String shareCode=null;
        if (codeType==1){
            shareCode= RandomUtil.randomString(5);
        }else {
            shareCode = code;
        }
        FileShare fileShare = minioService.fileShare(userId, filePath, shareValidType,shareCode);
        String webShareUrl = webAddr+DEFAULT_WEB_SHARE_URL+shareId+"_"+userId;
        fileShare.setFileId(fileId);
        fileShare.setUserId(userId);
        fileShare.setShareId(shareId);
        fileShare.setValidType(validType);
        fileShare.setWebShareUrl(webShareUrl);
        fileShare.setCode(shareCode);
        fileShare.setShowCount(0);
        if ("".equals(fileShare.getSharePath())){
            fileShare.setSharePath(fileInfo.getFilePath());
        }else {
            fileShare.setSharePath(fileShare.getSharePath());
        }
        int count = fileShareMapper.insert(fileShare);
        if (count==0){
            throw new CommonException("生成分享链接失败");
        }
        FileShareDTO fileShareDTO = BeanUtil.copyProperties(fileShare, FileShareDTO.class);
        return CommonResult.success(fileShareDTO,"生成分享链接成功");
    }

    @Override
    public CommonResult<CommonPage<FileShareDTO>> getShareFileList(Integer pageNum, Integer pageSize) {
        String userId = UserThreadLocal.getUserId();
        PageHelper.startPage(pageNum,pageSize);
        FileShareExample fileShareExample = new FileShareExample();
        fileShareExample.createCriteria().andUserIdEqualTo(userId);
        String order = "share_time" + DEFAULT_DESC;
        fileShareExample.setOrderByClause(order);
        List<FileShare> fileShares =fileShareMapper.selectByExampleWithBLOBs(fileShareExample);
        List<FileShareDTO> fileShareDTOList = fileShares.stream()
                .map(fileShare -> {
                    String fileId = fileShare.getFileId();
                    FileShareDTO fileShareDTO = BeanUtil.copyProperties(fileShare, FileShareDTO.class);
                    FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
                    fileShareDTO.setFileName(fileInfo.getFileName());
                    fileShareDTO.setFileSize(fileInfo.getFileSize());
                    return fileShareDTO;
                }).collect(Collectors.toList());
        return CommonResult.success(CommonPage.restPage(fileShareDTOList));
    }

    @Override
    public CommonResult cancelShare(List<String> shareIds) {
        String userId = UserThreadLocal.getUserId();
        List<String> filePathList=new ArrayList<>();
        List<String> filePathListMoreThan30=new ArrayList<>();
        FileShareExample fileShareExample = new FileShareExample();
        fileShareExample.createCriteria().andShareIdIn(shareIds).andUserIdEqualTo(userId);
        List<FileShare> fileShares = fileShareMapper.selectByExample(fileShareExample);
        for (FileShare fileShare : fileShares) {
            if (ShareValidTypeEnums.DAY_1.getType().equals(fileShare.getValidType()) || ShareValidTypeEnums.DAY_7.getType().equals(fileShare.getValidType())){
                filePathList.add(fileShare.getSharePath());
            }else {
                filePathListMoreThan30.add(fileShare.getSharePath());
            }

        }
        //
        if (filePathList.size()>0){
            boolean isCancelShareList = minioService.cancelShareList(userId, filePathList, 1);
            if (!isCancelShareList){
                throw new CommonException("取消失败，再来一次?");
            }
        }
        if (filePathListMoreThan30.size()>0){
            boolean isCancelShareListMoreThan30=minioService.cancelShareListMoreThan30(filePathListMoreThan30);
            if (!isCancelShareListMoreThan30){
                throw new CommonException("取消失败，再来一次?");
            }
        }
        int count = fileShareMapper.deleteByExample(fileShareExample);
        if (count==0){
            throw new CommonException("取消失败，再来一次?");
        }
        return CommonResult.success(null,"取消成功");
    }
}
