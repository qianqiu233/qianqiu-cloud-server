package com.qianqiu.clouddisk.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.service.FilePreviewService;
import com.qianqiu.clouddisk.utils.FileAboutUtil;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import com.qianqiu.clouddisk.utils.enums.FileTypeEnums;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.PREVIEW_KEY;
import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.PREVIEW_KEY_TTL;


@Service
public class FilePreviewServiceImpl implements FilePreviewService {
    @Resource
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public CommonResult getVideoUrl(String fileId) {
        String userId = UserThreadLocal.getUserId();
        String key=PREVIEW_KEY+userId+":"+fileId;
        String redisSignatureUrl = stringRedisTemplate.opsForValue().get(key);
        //不是空的
        if (StrUtil.isNotBlank(redisSignatureUrl)){
            return CommonResult.success(redisSignatureUrl);
        }
        //是空的
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        String previewPath = fileInfo.getFilePath();
        //生成预签名
        String signatureUrl = getSignatureUrl(previewPath, userId, fileId);
        return CommonResult.success(signatureUrl);
    }

    @Override
    public CommonResult getFile(String fileId, HttpServletResponse response){
        String signatureUrl=null;
        String userId = UserThreadLocal.getUserId();
        String key=PREVIEW_KEY+userId+":"+fileId;
        String redisSignatureUrl = stringRedisTemplate.opsForValue().get(key);
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        if (StrUtil.isNotBlank(redisSignatureUrl)){
            signatureUrl=redisSignatureUrl;
        }else{
            String previewPath =fileInfo.getFilePath();
            signatureUrl= getSignatureUrl(previewPath, userId, fileId);
        }
        Integer fileType = fileInfo.getFileType();
        String fileName = fileInfo.getFileName();
        if (FileTypeEnums.PDF.getTypeCode().equals(fileType)){
            return CommonResult.success(signatureUrl,"渲染成功");
        }
        if (FileTypeEnums.TXT.getTypeCode().equals(fileType)||FileTypeEnums.CODE.getTypeCode().equals(fileType)){
            boolean txtAndCode = FileAboutUtil.getTxtAndCode(signatureUrl, response, fileInfo);
            if (!txtAndCode){
                throw new CommonException("渲染失败");
            }
            return CommonResult.success(fileName,"渲染成功");
        }
        if (FileTypeEnums.DOC.getTypeCode().equals(fileType)){
            boolean docx = FileAboutUtil.getDocx(signatureUrl, response, fileInfo);
            if (!docx){
                throw new CommonException("渲染失败");
            }
            return CommonResult.success(fileName,"渲染成功");
        }
        if (FileTypeEnums.EXCEL.getTypeCode().equals(fileType)){
            boolean excel = FileAboutUtil.getExcel(signatureUrl, response, fileInfo);
            if (!excel){
                throw new CommonException("渲染失败");
            }
            return CommonResult.success(fileName,"渲染成功");
        }

        return CommonResult.success(fileName,"渲染成功");
    }

    @Override
    public CommonResult getAudioUrl(String fileId) {
        String userId = UserThreadLocal.getUserId();
        String key=PREVIEW_KEY+userId+":"+fileId;
        String redisSignatureUrl = stringRedisTemplate.opsForValue().get(key);
        //不是空的
        if (StrUtil.isNotBlank(redisSignatureUrl)){
            return CommonResult.success(redisSignatureUrl);
        }
        //是空的
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        String previewPath = fileInfo.getFilePath();
        //生成预签名
        String signatureUrl = getSignatureUrl(previewPath, userId, fileId);
        return CommonResult.success(signatureUrl);
    }

    @Override
    public CommonResult getImage(String fileId) {
        String userId = UserThreadLocal.getUserId();
        String key=PREVIEW_KEY+userId+":"+fileId;
        String redisSignatureUrl = stringRedisTemplate.opsForValue().get(key);
        //不是空的
        if (StrUtil.isNotBlank(redisSignatureUrl)){
            return CommonResult.success(redisSignatureUrl);
        }
        //是空的
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        String previewPath = fileInfo.getFilePath();
        //生成预签名
        String signatureUrl = getSignatureUrl(previewPath, userId, fileId);
        return CommonResult.success(signatureUrl);
    }

    private String getSignatureUrl(String filePath,String userId,String fileId){
        //统一30分钟
        String signatureUrl= minioUtil.generatePresignedUrl(userId, filePath, Method.GET, 30, TimeUnit.MINUTES);
        String key=PREVIEW_KEY+userId+":"+fileId;
        stringRedisTemplate.opsForValue().set(key,signatureUrl,PREVIEW_KEY_TTL,TimeUnit.MINUTES);
        return signatureUrl;

    }






}
