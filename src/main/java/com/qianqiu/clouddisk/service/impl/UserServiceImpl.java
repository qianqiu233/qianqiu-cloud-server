package com.qianqiu.clouddisk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.qianqiu.clouddisk.mbg.mbg_mapper.*;
import com.qianqiu.clouddisk.mbg.mbg_model.*;
import com.qianqiu.clouddisk.model.dto.MinioUploadDTO;
import com.qianqiu.clouddisk.model.dto.UpdateUserPwdDTO;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.model.vo.UserSpaceInfoVo;
import com.qianqiu.clouddisk.service.FileInfoService;
import com.qianqiu.clouddisk.service.MinioService;
import com.qianqiu.clouddisk.service.UserService;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private MinioService minioService;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private RoleMapper roleMapper;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CommonResult<String> getAvatar() {
        String userId = UserThreadLocal.getUserId();
        //先去redis获取
        String token = UserThreadLocal.getToken();
        String key = USER_INFO_KEY + token;
        String redisUserInfo = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(redisUserInfo)) {
            UserInfoVo userInfoVo = JSONUtil.toBean(redisUserInfo, UserInfoVo.class);
            return CommonResult.success(userInfoVo.getAvatarUrl());
        }
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        String avatarUrl = userInfo.getAvatarUrl();
        System.out.println(avatarUrl);
        return CommonResult.success(avatarUrl);
    }

    @Override
    public CommonResult updateUserAvatar(MultipartFile file) throws IOException {
        // todo 还需判断数据库是否存在相同头像，存在，就直接将历史头像更新状态就好
        String userId = UserThreadLocal.getUserId();
        //获取md5值唯一标识
        byte[] fileBytes = file.getBytes();
        String fileMd5 = DigestUtil.md5Hex(fileBytes);
        MinioUploadDTO minioUploadDto = minioService.uploadAvatar(file, userId);
        if (minioUploadDto == null) {
            return CommonResult.failed("更新头像失败");
        }
        int saveCount = fileInfoService.saveAvatarToDB(minioUploadDto, userId, fileMd5);
        if (saveCount < 1) {
            return CommonResult.failed("更新头像失败");
        }
        //根据id修改数据库的avatarurl
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setAvatarUrl(minioUploadDto.getFileUrl());
        userInfo.setUpdateTime(new Date());

        int count = userInfoMapper.updateByPrimaryKeySelective(userInfo);
        if (count < 1) {
            return CommonResult.failed("更新头像失败");
        }
        //删除redis内容
        String token = UserThreadLocal.getToken();
        String key = USER_INFO_KEY + token;
        stringRedisTemplate.delete(key);
        //重新存入
        UserInfoVo userInfoVo = selectUserInfo(userId);
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(userInfoVo), USER_INFO_KEY_TTL, TimeUnit.MINUTES);
        return CommonResult.success(minioUploadDto, "头像更新成功");
    }

    /**
     * 登录后修改密码
     *
     * @param updateUserPwdDTO
     * @return
     */
    @Override
    public CommonResult updateUserPassword(UpdateUserPwdDTO updateUserPwdDTO) {
        String userId = UserThreadLocal.getUserId();
        String oldPassword = updateUserPwdDTO.getOldPassword();
        String newPassword = updateUserPwdDTO.getNewPassword();
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo == null) {
            return CommonResult.failed("用户不存在");
        }
        if (!userInfo.getPassword().equals(oldPassword)) {
            return CommonResult.failed("密码错误，请重试");
        }
        userInfo.setPassword(newPassword);
        userInfo.setUpdateTime(new Date());
        int count = userInfoMapper.updateByPrimaryKeySelective(userInfo);
        if (count == 0) {
            return CommonResult.failed("更新密码失败");
        }
        return CommonResult.success(null, "更新密码成功");
    }

    @Override
    public CommonResult userLogout(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String tokenKey = USER_INFO_KEY + token;
        stringRedisTemplate.delete(tokenKey);
        return CommonResult.success(null, "退出成功,请重新登录");
    }

    @Override
    public UserInfoVo selectUserInfo(String userId) {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        UserInfoVo userInfoVo = BeanUtil.copyProperties(userInfo, UserInfoVo.class);
//        查找用户对应的角色
        UserRoleExample userRoleExample = new UserRoleExample();
        RolePermissionExample rolePermissionExample = new RolePermissionExample();

        userRoleExample.createCriteria().andUserIdEqualTo(userId);
        List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
        //权限表
        List<String> permissionCodeList = new ArrayList<>();
        //查找角色id
        List<Integer> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        //角色名字
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andRoleIdIn(roleIds);
        List<Role> roles = roleMapper.selectByExample(roleExample);
        List<String> roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.toList());
        userInfoVo.setRoleList(roleNames);
        rolePermissionExample.createCriteria().andRoleIdIn(roleIds);
        //获取到用户的角色
        List<RolePermission> rolePermissions = rolePermissionMapper.selectByExample(rolePermissionExample);
        //遍历获取权限
        for (RolePermission rolePermission : rolePermissions) {
            Integer permissionId = rolePermission.getPermissionId();
            Permission permission = permissionMapper.selectByPrimaryKey(permissionId);
            permissionCodeList.add(permission.getPermissionCode());
        }
        userInfoVo.setUserResources(permissionCodeList);
        return userInfoVo;
    }

    @Override
    public CommonResult getUseSpace() {
        Long useSpace = null;
        Long totalSpace = null;
        String userId = UserThreadLocal.getUserId();
        String key = USER_SPACE_KEY + userId;
        UserSpaceInfoVo userSpaceInfoByRedis = getUserSpaceInfoByRedis(key);
        if (userSpaceInfoByRedis!=null){
            useSpace = userSpaceInfoByRedis.getUseSpace();
            totalSpace = userSpaceInfoByRedis.getTotalSpace();
            UserSpaceInfoVo userSpaceInfoVo = UserSpaceInfoVo.builder()
                    .useSpace(useSpace)
                    .totalSpace(totalSpace)
                    .build();
            return CommonResult.success(userSpaceInfoVo);
        }
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        useSpace=userInfo.getUseSpace();
        totalSpace=userInfo.getTotalSpace();
        UserSpaceInfoVo userSpaceInfoVo = UserSpaceInfoVo.builder()
                .useSpace(useSpace)
                .totalSpace(totalSpace)
                .build();
        //重新设置
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(userSpaceInfoVo),USER_SPACE_KEY_TTL,TimeUnit.MINUTES);
        return CommonResult.success(userSpaceInfoVo);
    }

    /**
     * 更新用户空间
     *
     * @param fileInfoList 进行更新操作的文件
     * @param incOrDec     增加或减少 0/1
     * @return true/false
     */
    @Override
    public Boolean UpdateUserSpace(List<FileInfo> fileInfoList, int incOrDec) {
        Long allFileSizeSum = computeAllFileSize(fileInfoList);
        String userId = UserThreadLocal.getUserId();
        String key = USER_SPACE_KEY + userId;
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        Long useSpace = userInfo.getUseSpace();
        Long newUseSpace=0L;
        if (incOrDec==0){
            //增加
            newUseSpace=useSpace+allFileSizeSum;
        }else {
            newUseSpace=useSpace-allFileSizeSum;
        }
        //存入数据库，不需要修改时间，进行文件操作的时候修改过
        userInfo.setUseSpace(newUseSpace);
        int count = userInfoMapper.updateByPrimaryKey(userInfo);
        if (count==0){
            return false;
        }
        //更新缓存
        stringRedisTemplate.delete(key);
        return true;
    }

    /**
     * 获取文件大小总和
     *
     * @param fileInfoList 进行更新操作的文件
     * @return 文件大小
     */
    public Long computeAllFileSize(List<FileInfo> fileInfoList) {
        return fileInfoList.stream()
                .filter(fileInfo -> fileInfo.getFileSize() != null) // 过滤掉文件大小为 null 的对象
                .mapToLong(FileInfo::getFileSize) // 提取每个 FileInfo 对象的文件大小
                .sum();
    }
    public UserInfoVo getInfoByKey(){
        UserInfoVo redisUserInfo=null;
        String userId = UserThreadLocal.getUserId();
        String key = USER_INFO_KEY + userId;
        String redisStr = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(redisStr)) {
            redisUserInfo= JSONUtil.toBean(redisStr, UserInfoVo.class);
        }
        return redisUserInfo;
    }
    public UserSpaceInfoVo getUserSpaceInfoByRedis(String key){
        UserSpaceInfoVo redisUserSpaceInfo=null;
        String redisStr = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(redisStr)) {
            redisUserSpaceInfo= JSONUtil.toBean(redisStr, UserSpaceInfoVo.class);
        }
        return redisUserSpaceInfo;
    }

}
