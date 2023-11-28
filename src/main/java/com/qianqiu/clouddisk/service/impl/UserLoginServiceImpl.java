package com.qianqiu.clouddisk.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.qianqiu.clouddisk.exception.CommonException;

import com.qianqiu.clouddisk.mbg.mbg_mapper.UserInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.UserInfo;
import com.qianqiu.clouddisk.mbg.mbg_model.UserInfoExample;
import com.qianqiu.clouddisk.model.dto.*;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.service.UserLoginService;
import com.qianqiu.clouddisk.utils.CaptchaUtil;
import com.qianqiu.clouddisk.utils.MailUtil;
import com.qianqiu.clouddisk.utils.MinioUtil;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import com.wf.captcha.base.Captcha;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.*;

@Service
@Slf4j
public class UserLoginServiceImpl implements UserLoginService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private MinioUtil minioUtil;
    @Value("${minio.endpoint}")
    private String ENDPOINT;
    @Value("${minio.defaultBucketName}")
    private String defaultBucketName;
    @Override
    public void sendCode(HttpServletResponse response, String email, Integer type) {
        Captcha captcha=null;
        try {
            captcha= new CaptchaUtil().generateCaptcha(response,"png");
            captcha.out(response.getOutputStream());
            String code= captcha.text();
            // 初始栏验证码
            if (type==null||type==0){
                String key=LOGIN_CODE_KEY+email;
                stringRedisTemplate.opsForValue().set(key,code,LOGIN_CODE_KEY_TTL, TimeUnit.MINUTES);
            }else {
                String emailKey=LOGIN_CODE_EMAIL_KEY+email;
                stringRedisTemplate.opsForValue().set(emailKey,code,LOGIN_CODE_EMAIL_KEY_TTL,TimeUnit.MINUTES);
            }
            log.info("验证码:{}", code);
        } catch (IOException e) {
            throw new CommonException("验证码生成失败");
        }
    }

    @Override
    public int UserRegister(RegisterDTO registerDTO) {
        String nickName = registerDTO.getNickName();
        String email = registerDTO.getEmail();
        String checkCode = registerDTO.getCheckCode();
        String password = registerDTO.getPassword();
        String key=LOGIN_CODE_KEY+email;
        String code = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(code)||!code.equalsIgnoreCase(checkCode)){
            throw new CommonException("验证码错误");
        }
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.createCriteria().andEmailEqualTo(email);
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
        String md5Pwd = DigestUtil.md5Hex(password);
        userInfo.setPassword(md5Pwd);
        userInfo.setNickName(nickName);
        userInfo.setStatus(1);
        userInfo.setTotalSpace(5*1024*1024L);
        userInfo.setUseSpace(0L);
        minioUtil.createBucket(userId);
        String avatarUrl=ENDPOINT + "/" + defaultBucketName+"/default.png";
        userInfo.setAvatarUrl(avatarUrl);
//        TODO 到时候返回值得换
        int count = userInfoMapper.insert(userInfo);
        return count;
    }

    @Override
    public void sendEmailCode(SendEmailDTO sendEmailDTO) {
        String email = sendEmailDTO.getEmail();
        String checkCode = sendEmailDTO.getCheckCode();
        Integer type = sendEmailDTO.getType();
        String key=LOGIN_CODE_EMAIL_KEY+email;
        String code = stringRedisTemplate.opsForValue().get(key);
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
        String emailCode = RandomUtil.randomNumbers(6);
        mailUtil.sendSampleMail(email,"验证码",emailCode);
        String emailKey=LOGIN_CODE_EMAIL_KEY+email;
        stringRedisTemplate.opsForValue().set(emailKey,emailCode,LOGIN_CODE_EMAIL_KEY_TTL,TimeUnit.MINUTES);
    }

    @Override
    public UserInfoVo login(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String checkCode = loginDTO.getCheckCode();
        String password = loginDTO.getPassword();
        String key=LOGIN_CODE_KEY+email;
        String redisCode = stringRedisTemplate.opsForValue().get(key);
        if (!checkCode.equalsIgnoreCase(redisCode)){
            throw new CommonException("验证码错误");
        }
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.createCriteria().andEmailEqualTo(email);
        List<UserInfo> userInfos = userInfoMapper.selectByExample(userInfoExample);
        UserInfo userInfo = userInfos.get(0);
        if (userInfo==null){
            return null;
        }
        String dbPwd = userInfo.getPassword();
        if (!dbPwd.equals(password)){
            throw new CommonException("密码错误");
        }
        String userId = userInfo.getUserId();
        String nickName = userInfo.getNickName();
        Object avatar = userInfo.getAvatarUrl();
        UserInfoVo userInfoVo = UserInfoVo.builder()
                .userId(userId)
                .admin(false)
                .nickName(nickName)
                .avatar(avatar).build();
        UserThreadLocal.saveUser(userInfoVo);
        System.out.println(UserThreadLocal.getUser());
        return userInfoVo;
    }

    @Override
    public void reSetPassword(ReSetPwdDTO reSetPwdDTO) {
        String email = reSetPwdDTO.getEmail();
        String emailCode = reSetPwdDTO.getEmailCode();
        String checkCode = reSetPwdDTO.getCheckCode();
        String password = reSetPwdDTO.getPassword();
        String key=LOGIN_CODE_KEY+email;
        String redisCode = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(redisCode)){
            throw new CommonException("验证码失效");
        }
        if (!checkCode.equals(redisCode)){
            throw new CommonException("验证码错误");
        }
        String emailKey=LOGIN_CODE_EMAIL_KEY+email;
        String redisEmailCode = stringRedisTemplate.opsForValue().get(emailKey);
        if (StrUtil.isBlank(redisEmailCode)){
            throw new CommonException("邮箱验证码失效");
        }
        if (emailCode.equals(redisEmailCode)){
            throw new CommonException("邮箱验证码错误");
        }
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.createCriteria().andEmailEqualTo(email);
        UserInfo userInfo = userInfoMapper.selectByExample(userInfoExample).get(0);
        String dbPwd = userInfo.getPassword();
        if (StrUtil.isBlank(dbPwd)){
            throw new CommonException("密码存在问题");
        }
        if (!password.equals(dbPwd)){
            throw new CommonException("密码校验失败");
        }
    }
}
