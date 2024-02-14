package com.qianqiu.clouddisk.test;

import com.qianqiu.clouddisk.mbg.mbg_mapper.FileInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_mapper.UserInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfoExample;
import com.qianqiu.clouddisk.mbg.mbg_model.UserInfo;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.service.UserService;
import com.qianqiu.clouddisk.service.impl.UserServiceImpl;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_USER_TOTAL_SPACE;
import static com.qianqiu.clouddisk.utils.Constant.RedisConstant.USER_INFO_KEY;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private FileInfoMapper fileInfoMapper;
    void updateUserAvatarTest() throws Exception {
        String filePath = "C:\\Users\\27996\\Desktop\\test\\测试.PNG";
        File file=new File(filePath);
        MultipartFile multipartFile=new MockMultipartFile(
                "CES",
                file.getName(),
                "image/png",
                new FileInputStream(file)

        );

        CommonResult commonResult = userService.updateUserAvatar(multipartFile);
        System.out.println(commonResult.getCode());
        System.out.println(commonResult.getMsg());
        System.out.println(commonResult.getData());
    }
    @Test
    void test(){
        UserInfoVo user = UserThreadLocal.getUser();
        System.out.println(user);
    }
    @Test
    void test2(){
        String userId = "f85966923c7f4ffea07121ed2e9cb7c0";
        FileInfoExample fileInfoExample = new FileInfoExample();
        fileInfoExample.createCriteria().andUserIdEqualTo(userId);
        List<FileInfo> fileInfoList = fileInfoMapper.selectByExample(fileInfoExample);
        Long sum=0L;
        for (FileInfo fileInfo : fileInfoList) {
            Long fileSize = fileInfo.getFileSize();
            if (fileSize!=null){
                sum=fileSize+sum;
            }
        }
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        userInfo.setUseSpace(sum);
        userInfo.setTotalSpace(DEFAULT_USER_TOTAL_SPACE);
        userInfoMapper.updateByPrimaryKey(userInfo);
        System.out.println(sum);

    }
}
