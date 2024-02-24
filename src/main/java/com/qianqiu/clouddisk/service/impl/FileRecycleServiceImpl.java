package com.qianqiu.clouddisk.service.impl;

import com.github.pagehelper.PageHelper;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfoExample;
import com.qianqiu.clouddisk.service.FileInfoService;
import com.qianqiu.clouddisk.service.FileRecycleService;
import com.qianqiu.clouddisk.service.FileRecycleService;
import com.qianqiu.clouddisk.service.MinioService;
import com.qianqiu.clouddisk.utils.FileAboutUtil;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import com.qianqiu.clouddisk.utils.enums.FileFolderType;
import com.qianqiu.clouddisk.utils.enums.FileUseFlagEnums;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_DESC;
import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_SORT_FIELD;
@Slf4j
@Service
public class FileRecycleServiceImpl implements FileRecycleService {
    @Resource
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private MinioService minioService;
    @Autowired
    private FileInfoService fileInfoService;

    @Override
    public CommonResult<CommonPage<FileInfo>> getRecycleFileList(Integer pageNum, Integer pageSize) {
        String userId = UserThreadLocal.getUserId();
        //判断是否有文件过期，获取过期列表
        int count = deleteExpiredFiles(userId);
        PageHelper.startPage(pageNum, pageSize);
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
        String order = DEFAULT_SORT_FIELD + DEFAULT_DESC;
        fileInfoExample.setOrderByClause(order);
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        if (count==-1){
            return CommonResult.success(CommonPage.restPage(fileInfoList),"部分文件异常，请刷新");
        }
        return CommonResult.success(CommonPage.restPage(fileInfoList),count+"个文件过期");
    }

    @Override
    public CommonResult delRecycleFileById(String fileId) {
        String userId = UserThreadLocal.getUserId();
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        if (fileInfo != null && FileUseFlagEnums.RECYCLE.getFlag().equals(fileInfo.getUseFlag())) {
            //判断该文件是文件还是目录，是目录还需要查询目录下在回收站的文件
            List<String> delFilePaths = new ArrayList<>();
            List<String> delFileIds = new ArrayList<>();
            List<FileInfo> fileList = new ArrayList<>();
            if (FileFolderType.DIRECTORY.getFolderCode().equals(fileInfo.getFolderType())) {
                //是目录
                //该目录下所有文件
                List<FileInfo> fileAndChild = fileInfoService.getFileAndChild(fileInfo, fileList);
                for (FileInfo info : fileAndChild) {
                    delFilePaths.add(info.getFilePath());
                    delFileIds.add(info.getFileId());
                }
            } else {
                //只是文件
                delFileIds.add(fileInfo.getFileId());
                delFilePaths.add(fileInfo.getFilePath());
            }
            //删除minio里的文件
            Boolean isDel = minioService.delFiles(userId, delFilePaths);
            if (!isDel) {
                throw new CommonException("删除文件失败，请重试");
            }
            //删除数据库
            FileInfoExample fileInfoExample = new FileInfoExample();
            fileInfoExample.createCriteria()
                    .andFileIdIn(delFileIds)
                    .andUserIdEqualTo(userId)
                    .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
            int count = fileInfoMapper.deleteByExample(fileInfoExample);
            if (count == 0) {
                throw new CommonException("删除文件失败，请重试");
            }
            return CommonResult.success(count, "删除成功");
        }
        return CommonResult.failed("删除失败，文件不存在");
    }

    @Override
    public CommonResult delRecycleFileList(List<String> fileIds) {
        String userId = UserThreadLocal.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andFileIdIn(fileIds)
                .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        List<String> delFilePaths = new ArrayList<>();
        List<String> delFileIds = new ArrayList<>();
        List<FileInfo> fileList = new ArrayList<>();
        if (fileInfoList.size() > 0) {
            fileInfoExample.clear();
            for (FileInfo fileInfo : fileInfoList) {
                //判断当前目录/文件是否在回收站
                if (fileInfo != null && FileUseFlagEnums.RECYCLE.getFlag().equals(fileInfo.getUseFlag())) {
                    if (FileFolderType.DIRECTORY.getFolderCode().equals(fileInfo.getFolderType())) {
                        //是目录
                        //该目录下所有文件
                        List<FileInfo> fileAndChild = fileInfoService.getFileAndChild(fileInfo, fileList);
                        for (FileInfo info : fileAndChild) {
                            delFilePaths.add(info.getFilePath());
                            delFileIds.add(info.getFileId());
                        }
                    } else {
                        //只是文件
                        delFileIds.add(fileInfo.getFileId());
                        delFilePaths.add(fileInfo.getFilePath());
                    }
                }
            }
            //删除minio里的文件
            Boolean isDel = minioService.delFiles(userId, delFilePaths);
            if (!isDel) {
                throw new CommonException("删除文件失败，请重试");
            }
            //删除数据库
            fileInfoExample.createCriteria()
                    .andFileIdIn(delFileIds)
                    .andUserIdEqualTo(userId)
                    .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
            int count = fileInfoMapper.deleteByExample(fileInfoExample);
            if (count == 0) {
                throw new CommonException("删除文件失败，请重试");
            }
            return CommonResult.success(count, "删除成功");
        }
        return CommonResult.failed("删除失败，文件不存在");
    }
    @Override
    public CommonResult recoverFileById(String fileId) {
        String userId = UserThreadLocal.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria()
                .andFileIdEqualTo(fileId)
                .andUserIdEqualTo(userId)
                .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        if (fileInfoList.size()>0){
            fileInfoExample.clear();
            FileInfo fileInfo = fileInfoList.get(0);
            FileInfo fileInfoCondition = new FileInfo();
            fileInfoCondition.setUseFlag(FileUseFlagEnums.USING.getFlag());
            if (FileFolderType.DIRECTORY.getFolderCode().equals(fileInfo.getFolderType())) {
                List<FileInfo> fileList = new ArrayList<>();
                fileList = fileInfoService.getFileAndChild(fileInfo, fileList);
                List<String> fileIds = fileList.stream().map(FileInfo::getFileId).toList();
                fileInfoExample.createCriteria()
                        .andFileIdIn(fileIds)
                        .andUserIdEqualTo(userId)
                        .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
                int count = fileInfoMapper.updateByExampleSelective(fileInfoCondition, fileInfoExample);
                if (count == 0) {
                    throw new CommonException("还原文件失败，请重试");
                }
                return CommonResult.success(count, "还原成功");
            }else{
                fileInfoExample.createCriteria()
                        .andFileIdEqualTo(fileId)
                        .andUserIdEqualTo(userId)
                        .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
                fileInfoCondition.setFilePid("0");
                int count = fileInfoMapper.updateByExampleSelective(fileInfoCondition, fileInfoExample);
                if (count == 0) {
                    throw new CommonException("还原文件失败，请重试");
                }
                return CommonResult.success(count, "还原成功");
            }
        }
        return CommonResult.failed("还原失败，文件不存在");
    }
    @Override
    public CommonResult recoverFileList(List<String> fileIds) {
        String userId = UserThreadLocal.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andFileIdIn(fileIds)
                .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        FileInfo fileInfoCondition = new FileInfo();
        fileInfoCondition.setUseFlag(FileUseFlagEnums.USING.getFlag());
        if (fileInfoList.size()>0){
            fileInfoExample.clear();
            List<FileInfo> fileList = new ArrayList<>();
            for (FileInfo fileInfo : fileInfoList) {
                if (FileFolderType.DIRECTORY.getFolderCode().equals(fileInfo.getFolderType())) {
                    fileList = fileInfoService.getFileAndChild(fileInfo, fileList);
                }else {
                    fileInfo.setFilePid("0");
                    fileList.add(fileInfo);
                }
            }
            fileIds = fileList.stream().map(FileInfo::getFileId).collect(Collectors.toList());
            fileInfoExample.createCriteria()
                    .andUserIdEqualTo(userId)
                    .andFileIdIn(fileIds)
                    .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
            int count = fileInfoMapper.updateByExampleSelective(fileInfoCondition, fileInfoExample);
            if (count == 0) {
                throw new CommonException("还原文件失败，请重试");
            }
            return CommonResult.success(count, "还原成功");
        }
        return CommonResult.failed("还原失败，文件不存在");
    }
    private int deleteExpiredFiles(String userId){
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        List<String> delFileIds=new ArrayList<>();
        List<String> delFilePaths=new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            Date recoveryTime = fileInfo.getRecoveryTime();
            boolean expiredFile = FileAboutUtil.isExpiredFile(recoveryTime);
            if (!expiredFile){
                //没过期
                continue;
            }
            //过期了
            delFileIds.add(fileInfo.getFileId());
            delFilePaths.add(fileInfo.getFilePath());
        }
        //开始删除
        //删除minio里的文件
        if (delFilePaths.size()>0&&delFileIds.size()>0){
            Boolean isDel = minioService.delFiles(userId, delFilePaths);
            if (isDel){
                //在删除数据库，不用报错
                fileInfoExample.clear();
                fileInfoExample.createCriteria()
                        .andFileIdIn(delFileIds)
                        .andUserIdEqualTo(userId)
                        .andUseFlagEqualTo(FileUseFlagEnums.RECYCLE.getFlag());
                int count = fileInfoMapper.deleteByExample(fileInfoExample);
                log.info("共有{}个文件过期，已执行删除",count);
                log.info("删除的文件id:{}",delFileIds);
                log.info("删除的文件路径:{}",delFilePaths);

                return count;
            }
            return -1;
        }
        return 0;
    }

}
