package com.qianqiu.clouddisk.test;

import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.service.UserService;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Test
    void updateUserAvatarTest() throws Exception {
        String filePath = "C:\\Users\\27996\\Desktop\\test\\测试.PNG";
        File file=new File(filePath);
        MultipartFile multipartFile=new MockMultipartFile(
                "CES",
                file.getName(),
                "image/png",
                new FileInputStream(file)

        );
        CommonResult commonResult = userService.updateUserAvatar(multipartFile,"f85966923c7f4ffea07121ed2e9cb7c0");
        System.out.println(commonResult.getCode());
        System.out.println(commonResult.getMsg());
        System.out.println(commonResult.getData());
    }
    @Test
    void test(){
        UserInfoVo user = UserThreadLocal.getUser();
        System.out.println(user);
    }
}
