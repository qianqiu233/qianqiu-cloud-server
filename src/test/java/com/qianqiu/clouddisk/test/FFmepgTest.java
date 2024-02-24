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
        String videoPath="http://192.168.88.128:9000/72a3a681f03e47999e7826e450ba9895/image/8fcec1188b59b558d6a08c4de372b442_3639116082050167301.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20240223%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240223T141023Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=6da4de15b09c34de7af1b4fdd543b2d1f069dd9396ff7f9da6e9bfd89dc7a82e";
        String path= "http://www.baidu.com";
        boolean fileAccessible = fFmepgUtil.isFileAccessible(videoPath);
        System.out.println(fileAccessible);
    }
    @Test
    void test3(){
        String path="http://192.168.88.128:9000/a0548bd141d34411ac46358fe84a1838//image/5d77ab59f3f54902c3edb62874748040384628005.gif%40%21web-article-pic.webp?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20240224%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240224T084534Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=fe76b3cf7f44c46c2e56542e7654fd71e187311829e1e910327ed5d2a20f3b69";
        boolean thumbnail = fFmepgUtil.generateImageThumbnail(path, "5d77ab59f3f54902c3edb62874748040384628005.gif@!web-article-pic.webp");
        System.out.println(thumbnail);

    }
}
