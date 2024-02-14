package com.qianqiu.clouddisk.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.qianqiu.clouddisk.exception.CommonException;

import com.qianqiu.clouddisk.mbg.mbg_mapper.UserInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_mapper.UserRoleMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.UserInfo;
import com.qianqiu.clouddisk.mbg.mbg_model.UserInfoExample;
import com.qianqiu.clouddisk.mbg.mbg_model.UserRole;
import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.model.vo.UserLoginVo;
import com.qianqiu.clouddisk.service.UserLoginService;
import com.qianqiu.clouddisk.service.UserService;
import com.qianqiu.clouddisk.utils.CaptchaUtil;
import com.qianqiu.clouddisk.utils.MailUtil;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.Regexs.RegexPatterns;
import com.qianqiu.clouddisk.utils.Regexs.RegexUtils;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import com.wf.captcha.base.Captcha;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.*;
import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.*;

@Service
@Slf4j
public class UserLoginServiceImpl implements UserLoginService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private MinioUtil minioUtil;
    @Value("${minio.endpoint}")
    private String ENDPOINT;
    @Value("${minio.defaultBucketName}")
    private String defaultBucketName;
    @Override
    public void sendCode(HttpServletResponse response, String key, Integer type) {
//        TODO 可能要改，如果一直点验证码，redis会出现一堆验证码，得想个办法
        Captcha captcha=null;
        try {
            captcha= new CaptchaUtil().generateCaptcha(response,"png");
            captcha.out(response.getOutputStream());
            String code= captcha.text();
            // 初始栏验证码 登录框的验证码
            if (type==null||type==0){
                String loginKey=LOGIN_CODE_KEY+key;
                stringRedisTemplate.opsForValue().set(loginKey,code,LOGIN_CODE_KEY_TTL, TimeUnit.MINUTES);
            }else {
//                邮箱框的验证码
                String emailKey=LOGIN_CODE_EMAIL_KEY+key;
                stringRedisTemplate.opsForValue().set(emailKey,code,LOGIN_CODE_EMAIL_KEY_TTL,TimeUnit.MINUTES);
            }
            log.info("验证码:{}", code);
        } catch (IOException e) {
            throw new CommonException("验证码生成失败");
        }
    }

    @Override
    public CommonResult UserRegister(RegisterDTO registerDTO) {
        String nickName = registerDTO.getNickName();
        String email = registerDTO.getEmail();
        String checkCode = registerDTO.getCheckCode();
        String password = registerDTO.getPassword();
        if(!RegexUtils.isMd5(password)){
            password=DigestUtil.md5Hex(password);
        }
        String key = registerDTO.getKey();
        String sendKey=LOGIN_CODE_KEY+key;
        String code = stringRedisTemplate.opsForValue().get(sendKey);
        if (StrUtil.isBlank(code)||!code.equalsIgnoreCase(checkCode)){
            throw new CommonException("验证码错误");
        }
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.createCriteria().andEmailEqualTo(email);
        //判断邮箱是否注册过
        long hasEmail = userInfoMapper.countByExample(userInfoExample);
        userInfoExample.clear();
        userInfoExample.createCriteria().andNickNameEqualTo(nickName);
        long hasNickName = userInfoMapper.countByExample(userInfoExample);
        userInfoExample.clear();
        if (hasEmail>0){
            throw new CommonException("邮箱账号已经存在");
        }
        if (hasNickName>0){
            throw new CommonException("昵称已经存在");
        }
        String emailCode = registerDTO.getEmailCode();
        String emailKey=LOGIN_CODE_EMAIL_KEY+email;
        String redisEmailCode = stringRedisTemplate.opsForValue().get(emailKey);
        if (!emailCode.equalsIgnoreCase(redisEmailCode)){
            throw new CommonException("邮箱验证码不正确");
        }
        UserInfo userInfo=new UserInfo();
        String userId = IdUtil.simpleUUID();
        userInfo.setUserId(userId);
        userInfo.setEmail(email);
        userInfo.setCreateTime(new Date());
        userInfo.setLastLoginTime(new Date());
        userInfo.setUpdateTime(new Date());
        userInfo.setPassword(password);
        userInfo.setNickName(nickName);
//        todo 到时后写成默认常量
        userInfo.setStatus(DEFAULT_USER_STATUS);
        userInfo.setTotalSpace(DEFAULT_USER_TOTAL_SPACE);
        userInfo.setUseSpace(DEFAULT_USER_USE_SPACE);
        minioUtil.createBucket(userId);
//        todo 到时这里得写一个专门的默认类
        String avatarUrl=ENDPOINT + defaultBucketName+"/default.png";
        userInfo.setAvatarUrl(avatarUrl);
        //设置角色权限
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(DEFAULT_USER_ROLE);

//        TODO 到时候返回值得换
        if (userInfoMapper.insertSelective(userInfo)==0){
            return CommonResult.failed("注册失败");
        }
        if (userRoleMapper.insertSelective(userRole)==0){
            return CommonResult.failed("注册失败");
        }

        return CommonResult.success(null,"注册成功");
    }

    @Override
    public void sendEmailCode(SendEmailDTO sendEmailDTO) {

        String email = sendEmailDTO.getEmail();
        String checkCode = sendEmailDTO.getCheckCode();
        Integer type = sendEmailDTO.getType();
        String key = sendEmailDTO.getKey();
        String sendEmailKey=LOGIN_CODE_EMAIL_KEY+key;
        String code = stringRedisTemplate.opsForValue().get(sendEmailKey);
        if (StrUtil.isBlank(code)||!code.equalsIgnoreCase(checkCode)){
            throw new CommonException("验证码错误");
        }
        //通过type判断是不是注册
        if (type==0){
            UserInfoExample userInfoExample = new UserInfoExample();
            userInfoExample.createCriteria().andEmailEqualTo(email);
            long count = userInfoMapper.countByExample(userInfoExample);
            if (count>0){
                throw new CommonException("邮箱已经存在");
            }
        }
        String emailCode = RandomUtil.randomNumbers(DEFAULT_CODE_NUM);
        mailUtil.sendSampleMail(email,"验证码",emailCode);
//        发送到邮箱的验证码
        String emailKey=LOGIN_CODE_EMAIL_KEY+email;
        stringRedisTemplate.opsForValue().set(emailKey,emailCode,LOGIN_CODE_EMAIL_KEY_TTL,TimeUnit.MINUTES);
    }

    @Override
    public CommonResult login(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String checkCode = loginDTO.getCheckCode();
        String password = loginDTO.getPassword();
        String key = loginDTO.getKey();
        String loginKey=LOGIN_CODE_KEY+key;
        String redisCode = stringRedisTemplate.opsForValue().get(loginKey);
        if (!checkCode.equalsIgnoreCase(redisCode)){
            throw new CommonException("验证码错误");
        }
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.createCriteria().andEmailEqualTo(email);
        List<UserInfo> userInfos = userInfoMapper.selectByExample(userInfoExample);
        UserInfo userInfo = userInfos.get(0);
        if (userInfo==null){
            return CommonResult.failed("用户不存在，请重新输入或注册");
        }
        String dbPwd = userInfo.getPassword();
        if (!dbPwd.equals(password)){
            throw new CommonException("密码错误");
        }
        String userId = userInfo.getUserId();
        UserInfoVo userInfoVo = userService.selectUserInfo(userId);
        //  todo 后面可能使用jwt 先随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        String tokenKey = USER_INFO_KEY + token;

        stringRedisTemplate.opsForValue().set(tokenKey, JSONUtil.toJsonStr(userInfoVo),USER_INFO_KEY_TTL,TimeUnit.MINUTES);
        //又多加了个奇怪的东西
        UserLoginVo userLoginVo = new UserLoginVo(userInfoVo,token);
        return CommonResult.success(userLoginVo);
    }

    @Override
    public CommonResult reSetPassword(ReSetPwdDTO reSetPwdDTO) {
        String email = reSetPwdDTO.getEmail();
        String emailCode = reSetPwdDTO.getEmailCode();
        String checkCode = reSetPwdDTO.getCheckCode();
        String password = reSetPwdDTO.getPassword();
        if(!RegexUtils.isMd5(password)){
            password=DigestUtil.md5Hex(password);
        }
        String key = reSetPwdDTO.getKey();
        String sendKey=LOGIN_CODE_KEY+key;
        String redisCode = stringRedisTemplate.opsForValue().get(sendKey);
        if (StrUtil.isBlank(redisCode)){
            throw new CommonException("验证码失效");
        }
        if (!checkCode.equals(redisCode)){
            throw new CommonException("验证码错误");
        }
        //拿到邮箱内的验证码
        String emailKey=LOGIN_CODE_EMAIL_KEY+email;
        String redisEmailCode = stringRedisTemplate.opsForValue().get(emailKey);
        if (StrUtil.isBlank(redisEmailCode)){
            throw new CommonException("邮箱验证码失效");
        }
        if (!emailCode.equals(redisEmailCode)){
            throw new CommonException("邮箱验证码错误");
        }
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.createCriteria().andEmailEqualTo(email);
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        userInfo.setPassword(password);
        userInfo.setUpdateTime(new Date());
        int count = userInfoMapper.updateByExampleSelective(userInfo, userInfoExample);
        if (count==0){
            return CommonResult.failed("重置密码失败");
        }
        return CommonResult.success(null,"重置密码成功");
    }
}
