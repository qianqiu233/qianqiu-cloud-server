package com.qianqiu.clouddisk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileShareMapper;
import com.qianqiu.clouddisk.mbg.mbg_mapper.UserInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.*;
import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.service.FileInfoService;
import com.qianqiu.clouddisk.service.MinioService;
import com.qianqiu.clouddisk.service.WebShareService;
import com.qianqiu.clouddisk.utils.FileAboutUtil;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.MyDateUtil;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import com.qianqiu.clouddisk.utils.enums.FileFolderType;
import com.qianqiu.clouddisk.utils.enums.FileTypeEnums;
import com.qianqiu.clouddisk.utils.enums.FileUseFlagEnums;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_DESC;
import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_SORT_FIELD;
import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.*;
import static com.qianqiu.clouddisk.utils.enums.FileUseFlagEnums.USING;

@Service
public class WebShareServiceImpl implements WebShareService {
    @Resource
    private FileShareMapper fileShareMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MinioService minioService;

    @Override
    public CommonResult getShareLoginInfo( String webShareId) throws UnsupportedEncodingException {
        String[] s = webShareId.split("_");
        if (s.length != 2){
            return CommonResult.success(null);
        }
        String shareId=s[0];
        return  getShareInfo(shareId);

    }

    @Override
    public CommonResult getShareInfo(String shareId) {
        FileShare fileShare = fileShareMapper.selectByPrimaryKey(shareId);
        if (fileShare==null){
            throw new CommonException("该链接已经失效");
        }
        String userId = fileShare.getUserId();
        String fileId = fileShare.getFileId();
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo==null){
            throw new CommonException("该链接已经失效");
        }
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        if (fileInfo==null){
            throw new CommonException("该链接已经失效");
        }
        ShareInfo shareInfo = BeanUtil.copyProperties(fileShare, ShareInfo.class);
        shareInfo.setFileName(fileInfo.getFileName());
        shareInfo.setNickName(userInfo.getNickName());
        shareInfo.setAvatar(userInfo.getAvatarCover());
        return CommonResult.success(shareInfo);
    }

    @Override
    public CommonResult checkShareCode(CheckShareCodeDTO checkShareCodeDTO, HttpSession session) {
        String shareId = checkShareCodeDTO.getShareId();
        String code = checkShareCodeDTO.getCode();
        FileShare fileShare = fileShareMapper.selectByPrimaryKey(shareId);
        if (fileShare==null){
            return CommonResult.failed("该分享链接已失效");
        }
        String dbCoded = fileShare.getCode();
        if (!dbCoded.equals(code)) {
            return CommonResult.failed("提取码错误");
        }
        //访问次数+1
        Integer showCount = fileShare.getShowCount();
        showCount=showCount+1;
        fileShare.setShowCount(showCount);
        int count = fileShareMapper.updateByPrimaryKeySelective(fileShare);
        if (count==0){
            return CommonResult.failed("提取失败");
        }
        session.setAttribute("userId",fileShare.getUserId());
        return CommonResult.success(true,"提取成功");
    }

    @Override
    public CommonResult<CommonPage<WebFileInfoDTO>> selectSharedFile(SelectSharedFileDTO selectSharedFileDTO) {
        Integer pageNum = selectSharedFileDTO.getPageNum();
        Integer pageSize = selectSharedFileDTO.getPageSize();
        String filePid = selectSharedFileDTO.getFilePid();
        String shareId = selectSharedFileDTO.getShareId();
        PageHelper.startPage(pageNum,pageSize);
        FileShare fileShare = fileShareMapper.selectByPrimaryKey(shareId);
        if (fileShare==null){
            throw new CommonException("该分享链接已失效");
        }
        String fileId = fileShare.getFileId();
        String userId = fileShare.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        FileInfoExample.Criteria criteria = fileInfoExample.createCriteria();
        if (StrUtil.isNotBlank(filePid)&&!"0".equals(filePid)){
            criteria.andFilePidEqualTo(filePid);
        }else {
            criteria.andFileIdEqualTo(fileId);
        }
        criteria.andUserIdEqualTo(userId);
        criteria.andUseFlagEqualTo(FileUseFlagEnums.USING.getFlag());
        fileInfoExample.setOrderByClause(DEFAULT_SORT_FIELD+DEFAULT_DESC);
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExampleWithBLOBs(fileInfoExample);
        List<WebFileInfoDTO> webFileInfoDTOList =  fileInfoList.stream().map(item -> {
            WebFileInfoDTO webFileInfoDTO = BeanUtil.copyProperties(item, WebFileInfoDTO.class);
            return webFileInfoDTO;
        }).collect(Collectors.toList());
        return CommonResult.success(CommonPage.restPage(webFileInfoDTOList));
    }

    @Override
    public CommonResult getFolderInfo(GetFolderInfoDTO getFolderInfoDTO) {
        String path = getFolderInfoDTO.getPath();
        String[] paths = path.split("/");
        List<String> pathList = Arrays.asList(paths);
        String shareId = getFolderInfoDTO.getShareId();
        FileShare fileShare = fileShareMapper.selectByPrimaryKey(shareId);
        if (fileShare==null){
            throw new CommonException("分享链接已经失效");
        }
        String userId = fileShare.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria()
                .andFileIdIn(pathList)
                .andUseFlagEqualTo(USING.getFlag())
                .andUserIdEqualTo(userId)
                .andFolderTypeEqualTo(FileFolderType.DIRECTORY.getFolderCode());
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        return CommonResult.success(fileInfoList);
    }

    @Override
    public CommonResult getVideoUrl(String fileId, String shareId, String userId) {
        String urlBase = getUrlBase(fileId, shareId, userId);
        return CommonResult.success(urlBase);
    }

    @Override
    public CommonResult getAudioUrl(String fileId, String shareId, String userId) {
        String urlBase = getUrlBase(fileId, shareId, userId);
        return CommonResult.success(urlBase);
    }

    @Override
    public CommonResult getImage(String fileId, String shareId, String userId) {
        String urlBase = getUrlBase(fileId, shareId, userId);
        return CommonResult.success(urlBase);
    }

    @Override
    public CommonResult getFile(String fileId, String shareId, String userId, HttpServletResponse response) {
        String previewUrl = getUrlBase(fileId, shareId, userId);
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        Integer fileType = fileInfo.getFileType();
        String fileName = fileInfo.getFileName();
        if (FileTypeEnums.PDF.getTypeCode().equals(fileType)) {
            String encodePreviewUrl = previewUrl;
            return CommonResult.success(encodePreviewUrl, "渲染成功");
        }
        if (FileTypeEnums.TXT.getTypeCode().equals(fileType) || FileTypeEnums.CODE.getTypeCode().equals(fileType)) {
            boolean txtAndCode = FileAboutUtil.getTxtAndCode(previewUrl, response, fileInfo);
            if (!txtAndCode) {
                throw new CommonException("渲染失败");
            }
            return CommonResult.success(fileName, "渲染成功");
        }
        if (FileTypeEnums.DOC.getTypeCode().equals(fileType)) {
            boolean docx = FileAboutUtil.getDocx(previewUrl, response, fileInfo);
            if (!docx) {
                throw new CommonException("渲染失败");
            }
            return CommonResult.success(fileName, "渲染成功");
        }
        if (FileTypeEnums.EXCEL.getTypeCode().equals(fileType)) {
            boolean excel = FileAboutUtil.getExcel(previewUrl, response, fileInfo);
            if (!excel) {
                throw new CommonException("渲染失败");
            }
            return CommonResult.success(fileName, "渲染成功");
        }

        return CommonResult.success(fileName, "渲染成功");
    }

    @Override
    public CommonResult createDownloadToken(String fileId, String userId, String shareId) {
        FileShareExample fileShareExample = new FileShareExample();
        fileShareExample.createCriteria().andShareIdEqualTo(shareId);
        List<FileShare> fileShares = fileShareMapper.selectByExampleWithBLOBs(fileShareExample);
        //生成token
        String randomPart = RandomUtil.randomString(16); // 随机部分
        String timePart = MyDateUtil.format(new Date(), MyDateUtil.YMDHMS);  // 时间部分，这里用当前时间作为示例
        String webShareUrl ="";
        if (fileShares.size()==0){
            //说明是跟着包来的
            FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
            if (fileInfo==null||FileUseFlagEnums.RECYCLE.getFlag().equals(fileInfo.getUseFlag())){
                throw new CommonException("该文件可能已被删除");
            }
            //创建下载链接
            //为其生成访问链接
            webShareUrl= minioUtil.generatePresignedUrl(userId, fileInfo.getFilePath(), Method.GET, 30, TimeUnit.MINUTES);
        }else {
            //自己有链接
            FileShare fileShare = fileShares.get(0);
            if (fileShare==null){
                throw new CommonException("创建下载链接失败");
            }
            webShareUrl = fileShare.getShareUrl();
            if (StrUtil.isBlank(webShareUrl)){
                throw new CommonException("创建下载链接失败");
            }
        }
        //存入redis
        // 将随机部分和时间部分拼接成token
        String token = randomPart + "-" + timePart;
        //存入redis
        String key=WEB_SHARE_DOWNLOAD_KEY+token;
        stringRedisTemplate.opsForValue().set(key,webShareUrl,DOWNLOAD_KEY_TTL, TimeUnit.MINUTES);
        return CommonResult.success(token);
    }

    @Override
    public CommonResult download(String dowToken) {
        String key=WEB_SHARE_DOWNLOAD_KEY+dowToken;
        String downloadUrl = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(downloadUrl)){
            return CommonResult.failed("下载文件失败");
        }
        stringRedisTemplate.delete(key);
        return CommonResult.success(downloadUrl);
    }

    @Override
    public CommonResult saveWebShare(SaveWebShareDTO saveWebShareDTO) {
        List<String> shareFileIds = saveWebShareDTO.getShareFileIds();
        String myFolderId = saveWebShareDTO.getMyFolderId();
        String shareId = saveWebShareDTO.getShareId();
        String myUserId = UserThreadLocal.getUserId();
        FileShare fileShare = fileShareMapper.selectByPrimaryKey(shareId);
        String sourceUserId = fileShare.getUserId();
        FileInfoExample fileInfoExample = new FileInfoExample();
        FileInfoExample.Criteria criteria = fileInfoExample.createCriteria();
        criteria.andUserIdEqualTo(sourceUserId);
        criteria.andUseFlagEqualTo(USING.getFlag());
        List<FileInfo> copyFileList = new ArrayList<>();
        for (String shareFileId : shareFileIds) {
            criteria.andFileIdEqualTo(shareFileId);
            List<FileInfo> fileInfoList = fileInfoMapper.selectByExampleWithBLOBs(fileInfoExample);
            if (fileInfoList.size()==0){
                continue;
            }
            FileInfo fileInfo = fileInfoList.get(0);
            if (FileFolderType.DIRECTORY.getFolderCode().equals(fileInfo.getFolderType())){
                copyFileList= fileInfoService.getFileAndChild(fileInfo, copyFileList);

            }else {
                copyFileList.add(fileInfo);
            }
        }
        FileInfo fileInfo=null;
        String fileName=null;
        if (!myFolderId.equals("0")){
            fileInfo= fileInfoMapper.selectByPrimaryKey(myFolderId, myUserId);
             fileName= fileInfo.getFileName();
        }else {
            fileName="";
        }

        boolean flag=false;
        fileInfoExample.clear();
        //遍历拿到路径
        for (FileInfo info : copyFileList) {
            String copyFilePath= info.getFilePath();
            String targetFilePath = "/"+fileName+copyFilePath;
            String newUrl = minioService.copyBucketItem2OtherBucket(sourceUserId, copyFilePath, myUserId, targetFilePath);
            if (StrUtil.isBlank(newUrl)){
                flag=true;
                continue;
            }
            info.setFilePid(myFolderId);
            info.setUserId(myUserId);
            String fileId = IdUtil.simpleUUID();
            info.setFileId(fileId);
            info.setFileUrl(newUrl);
            info.setFilePath(targetFilePath);
            info.setCreateTime(new Date());
            info.setLastUpdateTime(new Date());
            //存储到数据库
            int count = fileInfoMapper.insert(info);
            if (count==0){
                throw new CommonException("保存失败");
            }
        }
        if (flag){
            throw new CommonException("部分保存失败");
        }

        return CommonResult.success(null,"保存成功");
    }

    public String getUrlBase(String fileId, String shareId, String userId){
        //判断视频是否有分享链接，有的可能是分享了包，里面文件并未通过预签名
        FileShareExample fileShareExample = new FileShareExample();
        fileShareExample.createCriteria().andFileIdEqualTo(fileId).andShareIdEqualTo(shareId);
        List<FileShare> fileShares = fileShareMapper.selectByExampleWithBLOBs(fileShareExample);
        if (fileShares.size()==0){
            //说明是在分享的包内的
            //创建分享链接
            //判断数据库是否存在该文件
            //看看redis有没有
            String key=PREVIEW_KEY+userId+":"+fileId;
            String redisSignatureUrl = stringRedisTemplate.opsForValue().get(key);
            //不是空的
            if (StrUtil.isNotBlank(redisSignatureUrl)){
                return redisSignatureUrl;
            }
            FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
            if (fileInfo==null||FileUseFlagEnums.RECYCLE.getFlag().equals(fileInfo.getUseFlag())){
                throw new CommonException("该文件可能已被删除");
            }
            //为其生成访问链接
            String signatureUrl= minioUtil.generatePresignedUrl(userId, fileInfo.getFilePath(), Method.GET, 30, TimeUnit.MINUTES);
            //用户的
            stringRedisTemplate.delete(key);
            stringRedisTemplate.opsForValue().set(key,signatureUrl,PREVIEW_KEY_TTL,TimeUnit.MINUTES);
            return signatureUrl;
        }
        FileShare fileShare = fileShares.get(0);
        String shareUrl = fileShare.getShareUrl();
        if (shareUrl == null){
            throw new CommonException("文件加载失败，可能已被取消分享");
        }
        return shareUrl;
    }
}
