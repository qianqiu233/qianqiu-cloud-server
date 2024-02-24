package com.qianqiu.clouddisk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.qianqiu.clouddisk.exception.CommonException;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileShareMapper;
import com.qianqiu.clouddisk.mbg.mbg_mapper.UserInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_mapper.UserRoleMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.*;
import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.service.AdminService;
import com.qianqiu.clouddisk.service.FileInfoService;
import com.qianqiu.clouddisk.service.MinioService;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.MyDateUtil;
import com.qianqiu.clouddisk.utils.FileAboutUtil;
import com.qianqiu.clouddisk.utils.commonResult.CommonPage;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import com.qianqiu.clouddisk.utils.enums.FileFolderType;
import com.qianqiu.clouddisk.utils.enums.FileTypeEnums;
import com.qianqiu.clouddisk.utils.enums.FileUseFlagEnums;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.*;
import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.*;
import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.DOWNLOAD_KEY_TTL;

@Service
public class AdminServiceImpl implements AdminService {
    @Resource
    private FileInfoMapper fileInfoMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private FileShareMapper fileShareMapper;
    @Autowired
    private MinioService minioService;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CommonResult<CommonPage<AdminFileInfoDTO>> getAdminFileInfoList(FileInfoDTO fileInfoDTO) {
        Integer pageNum = fileInfoDTO.getPageNum();
        Integer pageSize = fileInfoDTO.getPageSize();
        String fileNameFuzzy = fileInfoDTO.getFileNameFuzzy();
        String filePid = fileInfoDTO.getFilePid();
        PageHelper.startPage(pageNum, pageSize);
        FileInfoExample fileInfoExample = new FileInfoExample();
        FileInfoExample.Criteria criteria = fileInfoExample.createCriteria();
        criteria.andUseFlagEqualTo(FileUseFlagEnums.USING.getFlag());
        if (fileNameFuzzy != null) {
            criteria.andFileNameLike("%" + fileNameFuzzy + "%");
        }
        if (filePid != null) {
            criteria.andFilePidEqualTo(filePid);
        }
        String order = DEFAULT_SORT_FIELD + DEFAULT_DESC;
        fileInfoExample.setOrderByClause(order);
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExampleWithBLOBs(fileInfoExample);
        List<AdminFileInfoDTO> adminFileInfoDTOList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            AdminFileInfoDTO adminFileInfoDTO = BeanUtil.copyProperties(fileInfo, AdminFileInfoDTO.class);
            String userId = fileInfo.getUserId();
            UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
            adminFileInfoDTO.setNickName(userInfo.getNickName());
            adminFileInfoDTOList.add(adminFileInfoDTO);
        }
        return CommonResult.success(CommonPage.restPage(adminFileInfoDTOList));
    }

    @Override
    public CommonResult getVideoUrl(String fileId, String userId) {
        String url = hasAndGetShareUrl(fileId, userId);
        if (url!=null){
            return CommonResult.success(url);
        }
        //看看redis有没有
        String key=PREVIEW_KEY+userId+":"+fileId;
        String redisSignatureUrl = stringRedisTemplate.opsForValue().get(key);
        //不是空的
        if (StrUtil.isNotBlank(redisSignatureUrl)){
            return CommonResult.success(redisSignatureUrl);
        }
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        String signatureUrl= minioUtil.generatePresignedUrl(userId, fileInfo.getFilePath(), Method.GET, 30, TimeUnit.MINUTES);
        //用户的
        stringRedisTemplate.delete(key);
        stringRedisTemplate.opsForValue().set(key,signatureUrl,PREVIEW_KEY_TTL,TimeUnit.MINUTES);
        return CommonResult.success(signatureUrl);
    }

    @Override
    public CommonResult getAudioUrl(String fileId, String userId) {
        String url = hasAndGetShareUrl(fileId, userId);
        if (url!=null){
            return CommonResult.success(url);
        }
        //看看redis有没有
        String key=PREVIEW_KEY+userId+":"+fileId;
        String redisSignatureUrl = stringRedisTemplate.opsForValue().get(key);
        //不是空的
        if (StrUtil.isNotBlank(redisSignatureUrl)){
            return CommonResult.success(redisSignatureUrl);
        }
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        String signatureUrl= minioUtil.generatePresignedUrl(userId, fileInfo.getFilePath(), Method.GET, 30, TimeUnit.MINUTES);
        //用户的
        stringRedisTemplate.delete(key);
        stringRedisTemplate.opsForValue().set(key,signatureUrl,PREVIEW_KEY_TTL,TimeUnit.MINUTES);
        return CommonResult.success(signatureUrl);
    }

    @Override
    public CommonResult getFile(String fileId, String userId, HttpServletResponse response) {
        String previewUrl="";
        //是否在分享中
        String url = hasAndGetShareUrl(fileId, userId);
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        if (url!=null){
            previewUrl=url;
        }else{
            //看看redis有没有
            String key=PREVIEW_KEY+userId+":"+fileId;
            String redisSignatureUrl = stringRedisTemplate.opsForValue().get(key);
            //不是空的
            if (StrUtil.isNotBlank(redisSignatureUrl)){
                previewUrl=redisSignatureUrl;
            }else{
                String signatureUrl= minioUtil.generatePresignedUrl(userId, fileInfo.getFilePath(), Method.GET, 30, TimeUnit.MINUTES);
                //用户的
                stringRedisTemplate.delete(key);
                stringRedisTemplate.opsForValue().set(key,signatureUrl,PREVIEW_KEY_TTL,TimeUnit.MINUTES);
                previewUrl=signatureUrl;
            }
        }
        Integer fileType = fileInfo.getFileType();
        String fileName = fileInfo.getFileName();
        if (FileTypeEnums.PDF.getTypeCode().equals(fileType)) {
            String encodePreviewUrl = URLUtil.encode(previewUrl);
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
    public CommonResult delAdminFileById(AdminIdInfoDTO adminIdInfoDTO) {
        int count = delFileBase(adminIdInfoDTO);
        if (count == 0) {
            return CommonResult.failed("删除失败");
        }
        return CommonResult.success(count, "删除成功");
    }

    @Override
    public CommonResult delAdminFileList(List<AdminIdInfoDTO> listParams) {
        int sum = 0;
        boolean errorFlag = false;
        List<String> errorMsgList = new ArrayList<>();
        for (AdminIdInfoDTO listParam : listParams) {
            int count = delFileBase(listParam);
            if (count == 0) {
                errorFlag = true;
                String msg = "用户" + listParam.getUserId() + "的" + listParam.getFileId() + "文件删除失败";
                errorMsgList.add(msg);
            }
            sum = sum + count;
        }
        if (errorFlag) {
            return CommonResult.failed(errorMsgList, "部分删除失败");
        }
        return CommonResult.success(sum, "删除成功");
    }

    @Override
    public CommonResult<CommonPage<AdminUserListDTO>> getUserList(Integer pageNum, Integer pageSize, String nickNameFuzzy, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        UserInfoExample userInfoExample = new UserInfoExample();
        UserInfoExample.Criteria criteria = userInfoExample.createCriteria();
        if (StrUtil.isNotEmpty(nickNameFuzzy)){
            criteria.andNickNameLike("%"+nickNameFuzzy+"%");
        }
        if (status !=null){
            criteria.andStatusEqualTo(status);
        }
        userInfoExample.setOrderByClause("create_time"+DEFAULT_ASC);
        List<UserInfo> userInfos = userInfoMapper.selectByExampleWithBLOBs(userInfoExample);
        UserRoleExample userRoleExample = new UserRoleExample();
        List<AdminUserListDTO> adminUserListDTOList=new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            if (userInfo == null) {
                continue;
            }
            String userId = userInfo.getUserId();
            userRoleExample.createCriteria().andUserIdEqualTo(userId).andRoleIdEqualTo(1);
            //是管理员
            long count = userRoleMapper.countByExample(userRoleExample);
            if (count > 0) {
                continue;
            }
            //是用户
            Date createTime = userInfo.getCreateTime();
            String createTimeStr = MyDateUtil.format(createTime, MyDateUtil.YMDHM);
            Date LastLoginTime = userInfo.getLastLoginTime();
            String lineTimeStr = MyDateUtil.format(LastLoginTime, MyDateUtil.YMDHM);
            AdminUserListDTO adminUserListDTO = AdminUserListDTO.builder()
                    .userId(userInfo.getUserId())
                    .avatar(userInfo.getAvatarCover())
                    .email(userInfo.getEmail())
                    .createTime(createTimeStr)
                    .useSpace(userInfo.getUseSpace())
                    .totalSpace(userInfo.getTotalSpace())
                    .nickName(userInfo.getNickName())
                    .status(userInfo.getStatus())
                    .lastLoginTime(lineTimeStr).build();
            adminUserListDTOList.add(adminUserListDTO);
        }
        return CommonResult.success(CommonPage.restPage(adminUserListDTOList),"查询成功");
    }

    @Override
    public CommonResult getImage(String fileId, String userId) {
        String url = hasAndGetShareUrl(fileId, userId);
        if (url!=null){
            return CommonResult.success(url);
        }
        //看看redis有没有
        String key=PREVIEW_KEY+userId+":"+fileId;
        String redisSignatureUrl = stringRedisTemplate.opsForValue().get(key);
        //不是空的
        if (StrUtil.isNotBlank(redisSignatureUrl)){
            return CommonResult.success(redisSignatureUrl);
        }
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        String signatureUrl= minioUtil.generatePresignedUrl(userId, fileInfo.getFilePath(), Method.GET, 30, TimeUnit.MINUTES);
        //用户的
        stringRedisTemplate.delete(key);
        stringRedisTemplate.opsForValue().set(key,signatureUrl,PREVIEW_KEY_TTL,TimeUnit.MINUTES);
        return CommonResult.success(signatureUrl);
    }

    @Override
    public CommonResult updateUserStatus(UpdateUserStatusDTO updateUserStatusDTO) {
        String userId = updateUserStatusDTO.getUserId();
        Integer status = updateUserStatusDTO.getStatus();
        if (status==1||status==0){
            UserInfoExample userInfoExample = new UserInfoExample();
            userInfoExample.createCriteria().andUserIdEqualTo(userId);
            UserInfo userInfo = new UserInfo();
            userInfo.setStatus(status);
            int count = userInfoMapper.updateByExampleSelective(userInfo, userInfoExample);
            if (count==0){
                return CommonResult.failed("启用或禁用失败");
            }
            return CommonResult.success(count,"启用或禁用成功");
        }
        return CommonResult.failed("用户状态异常");
    }

    @Override
    public CommonResult updateUserSpace(UpdateUserSpaceDTO updateUserSpaceDTO) {
        String userId = updateUserSpaceDTO.getUserId();
        Long changeSpace = updateUserSpaceDTO.getChangeSpace()*DEFAULT_MB;
        Long totalSpace = updateUserSpaceDTO.getTotalSpace();
        Integer flag = updateUserSpaceDTO.getFlag();
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.createCriteria().andUserIdEqualTo(userId);
        UserInfo userInfo = new UserInfo();
        if (flag == 0){
            //扩容
            Long updateSpace=changeSpace+totalSpace;
            userInfo.setTotalSpace(updateSpace);
            int count = userInfoMapper.updateByExampleSelective(userInfo,userInfoExample);
            if (count==0){
                throw new CommonException("扩容失败");
            }
            return CommonResult.success(updateSpace,"扩容成功");
        }else if(flag == 1){
            //缩容
            Long updateSpace=totalSpace-changeSpace;
            userInfo.setTotalSpace(updateSpace);
            int count = userInfoMapper.updateByExampleSelective(userInfo,userInfoExample);
            if (count==0){
                throw new CommonException("缩容失败");
            }
            return CommonResult.success(updateSpace,"缩容成功");
        }
        return CommonResult.failed("参数错误");
    }

    @Override
    public CommonResult createDownloadToken(String fileId, String userId) {
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        if (fileInfo==null){
            throw new CommonException("创建下载链接失败，文件不存在");
        }
        //生成token
        String randomPart = RandomUtil.randomString(16); // 随机部分
        String timePart = MyDateUtil.format(new Date(), MyDateUtil.YMDHMS);  // 时间部分，这里用当前时间作为示例
        //生成一天的预签名，然后下载一次后，删除
        String signatureUrl = minioUtil.generatePresignedUrl(userId, fileInfo.getFilePath(), Method.GET, 30, TimeUnit.MINUTES);
        // 将随机部分和时间部分拼接成token
        String token = randomPart + "-" + timePart;
        //存入redis
        String key=ADMIN_DOWNLOAD_KEY+token;
        stringRedisTemplate.opsForValue().set(key,signatureUrl,DOWNLOAD_KEY_TTL, TimeUnit.MINUTES);
        return CommonResult.success(token);
    }

    @Override
    public CommonResult download(String dowToken) {
        String key=ADMIN_DOWNLOAD_KEY+dowToken;
        String downloadUrl = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(downloadUrl)){
            return CommonResult.failed("下载文件失败");
        }

        stringRedisTemplate.delete(key);
        return CommonResult.success(downloadUrl);
    }

    @Override
    public CommonResult getSysSettings() {
        String redisSystem = stringRedisTemplate.opsForValue().get(SYSTEM_KEY);
        if (StrUtil.isBlank(redisSystem)){
            SysSettingDTO sysSettingDTO = new SysSettingDTO();
            return CommonResult.success(sysSettingDTO);
        }
        SysSettingDTO sysSetting = JSONUtil.toBean(redisSystem, SysSettingDTO.class);
        return CommonResult.success(sysSetting);
    }

    @Override
    public CommonResult updateSysSettings(SysSettingDTO sysSettingDTO) {
        stringRedisTemplate.delete(SYSTEM_KEY);
        stringRedisTemplate.opsForValue().set(SYSTEM_KEY,JSONUtil.toJsonStr(sysSettingDTO));
        return CommonResult.success(null,"修改配置成功");
    }

    private int delFileBase(AdminIdInfoDTO adminIdInfoDTO) {
        String fileId = adminIdInfoDTO.getFileId();
        String userId = adminIdInfoDTO.getUserId();
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId, userId);
        if (fileInfo != null) {
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
                return 0;
            }
            //删除数据库
            FileInfoExample fileInfoExample = new FileInfoExample();
            fileInfoExample.createCriteria()
                    .andFileIdIn(delFileIds)
                    .andUserIdEqualTo(userId);
            int count = fileInfoMapper.deleteByExample(fileInfoExample);
            if (count == 0) {
                return count;
            }
            return count;
        }
        return 0;
    }

    private String hasAndGetShareUrl(String fileId,String userId){
        FileShareExample fileShareExample = new FileShareExample();
        fileShareExample.createCriteria().andFileIdEqualTo(fileId).andUserIdEqualTo(userId);
        List<FileShare> fileShares = fileShareMapper.selectByExample(fileShareExample);
        if (fileShares.size()>0){
            FileShare fileShare = fileShares.get(0);
            return fileShare.getShareUrl();
        }
        return null;
    }
}
