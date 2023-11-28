package com.qianqiu.clouddisk;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.qianqiu.clouddisk.mbg.mbg_model.UserInfo;
import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;

@SpringBootTest
class QianqiuCloudServerApplicationTests {

    @Test
    void contextLoads() {
        for (int i = 0; i < 10; i++) {
            String userId = IdUtil.simpleUUID();
            System.out.println(userId);
        }

    }
    @Test
    void test1(){
        String filePath1 = "C:\\Users\\27996\\Desktop\\test\\测试.PNG";
        String filePath2 = "C:\\Users\\27996\\Desktop\\测试.PNG";
        File file1=new File(filePath1);
        File file2=new File(filePath2);
        boolean b = FileUtil.contentEquals(file1, file2);
        System.out.println(b);
    }


}
