package com.qianqiu.clouddisk.test;

import com.qianqiu.clouddisk.utils.FFmepgUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_THUMBNAIL_PACKAGE;

@SpringBootTest
public class FFmepgTest {
    //http://192.168.88.128:9000/f85966923c7f4ffea07121ed2e9cb7c0/Minecraft_ 1.16.5 - 单人游戏 2023-07-14 13-32-00.mp4
    @Autowired
    private FFmepgUtil fFmepgUtil;
    @Test
    void  test01(){
        String videoPath="http://192.168.88.128:9000/f85966923c7f4ffea07121ed2e9cb7c0/Minecraft_ 1.16.5 - 单人游戏 2023-07-01 22-54-31.mp4";
        String picPath="Minecraft_ 1.16.5 - 单人游戏 2023-07-01 22-54-31.mp4.png";
        boolean b = fFmepgUtil.generateVideoThumbnail(videoPath, picPath);
        if (b){
            System.out.println("成功");
        }
    }
    @Test
    void test02(){
        String videoPath="http://192.168.88.128:9000/f85966923c7f4ffea07121ed2e9cb7c0/Minecraft_ 1.16.5 - 单人游戏 2023-07-01 22-54-31.mp4";
        String path= "http://www.baidu.com";
        boolean fileAccessible = fFmepgUtil.isFileAccessible(videoPath);
        System.out.println(fileAccessible);
    }
    @Test
    void test(){

    }
}
