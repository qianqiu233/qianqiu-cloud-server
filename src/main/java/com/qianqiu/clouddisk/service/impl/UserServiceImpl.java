package com.qianqiu.clouddisk.service.impl;

import com.qianqiu.clouddisk.mbg.mbg_mapper.UserInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.UserInfo;
import com.qianqiu.clouddisk.model.dto.MinioUploadDTO;
import com.qianqiu.clouddisk.model.dto.UpdateUserPwdDTO;
import com.qianqiu.clouddisk.service.UserService;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private MinioUtil minioUtil;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Override
    public CommonResult<String> getAvatar(String userId) {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        String avatarUrl = userInfo.getAvatarUrl();
        System.out.println(avatarUrl);
        return CommonResult.success(avatarUrl);
    }

    @Override
    public CommonResult updateUserAvatar(MultipartFile file,String userId) {
        MinioUploadDTO minioUploadDto = minioUtil.uploadFile(file, userId);
        if (minioUploadDto==null){
            return CommonResult.failed("更新头像失败");
        }
        //根据id修改数据库的avatarurl
        UserInfo userInfo = new UserInfo();
        userInfo.setAvatarUrl(userId);
        userInfo.setAvatarUrl(minioUploadDto.getFileUrl());
        userInfo.setUpdateTime(new Date());
        int count = userInfoMapper.updateByPrimaryKeySelective(userInfo);
        if (count==0){
            return CommonResult.failed("更新头像失败");
        }
        return CommonResult.success(minioUploadDto,"头像更新成功");
    }

    @Override
    public CommonResult updateUserPassword(UpdateUserPwdDTO updateUserPwdDTO) {
        String userId = updateUserPwdDTO.getUserId();
        String oldPassword = updateUserPwdDTO.getOldPassword();
        String newPassword = updateUserPwdDTO.getNewPassword();
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo==null){
            return CommonResult.failed("用户不存在");
        }
        if (!userInfo.getPassword().equals(oldPassword)){
            return CommonResult.failed("密码错误，请重试");
        }
        userInfo.setPassword(newPassword);
        userInfo.setUpdateTime(new Date());
        int count = userInfoMapper.updateByPrimaryKeySelective(userInfo);
        if (count==0){
            return CommonResult.failed("更新密码失败");
        }
        return CommonResult.success(null,"更新密码成功");
    }

}
