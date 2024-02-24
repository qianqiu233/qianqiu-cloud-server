package com.qianqiu.clouddisk;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.qianqiu.clouddisk.mbg.mbg_mapper.FileInfoMapper;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.service.FileInfoService;
import com.qianqiu.clouddisk.utils.MyDateUtil;
import com.qianqiu.clouddisk.utils.FFmepgUtil;
import com.qianqiu.clouddisk.utils.FileAboutUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_THUMBNAIL_PACKAGE;

@SpringBootTest
class QianqiuCloudServerApplicationTests {
    @Resource
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private FFmepgUtil fuUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private FileInfoService fileInfoService;

    @Test
    void contextLoads() {
        for (int i = 0; i < 10; i++) {
            String userId = IdUtil.simpleUUID();
            System.out.println(userId);
        }

    }

    @Test
    void test1() {
        String filePath1 = "C:\\Users\\27996\\Desktop\\test\\测试.PNG";
        String filePath2 = "C:\\Users\\27996\\Desktop\\测试.PNG";
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        boolean b = FileUtil.contentEquals(file1, file2);
        System.out.println(b);
    }

    @Test
    void testDate() {
        Date date = new Date();
        String format = MyDateUtil.format(date, MyDateUtil.YMD);
        String format1 = MyDateUtil.format(date, MyDateUtil.HMS);
        System.out.println(date);
        System.out.println(format);
        System.out.println(format1);
    }

    // ffmpeg -i C:\Users\27996\Videos\Captures\Minecraft_ 1.16.5 - 单人游戏 2023-07-14 13-38-35.mp4 -ss 00:00:01 -vframes 1 -y C:\Users\27996\Desktop\tttt\3.png
    @Test
    void ffmpegTest() {
        String videoPath = "C:\\Users\\27996\\Videos\\Captures\\Minecraft_ 1.16.5 - 单人游戏 2023-07-14 13-38-35.mp4";
        String thumbnailPath = "C:\\Users\\27996\\Desktop\\tttt\\5.png";
        String ffmepg_path = "E:\\JavaStudySource\\otherSoftware\\ffmpeg-6.1.1-essentials_build\\bin\\ffmpeg.exe";
        VideoToPicture(videoPath, ffmepg_path, thumbnailPath);
    }

    public boolean VideoToPicture(String veido_path, String ffmpeg_path, String picPath) {
        File file = new File(veido_path);
        if (!file.exists()) {
            System.err.println("路径[" + veido_path + "]对应的视频文件不存在!");
            return false;
        }
        //-i 参数后面跟着视频文件的路径。
        //-y 参数表示覆盖已存在的输出文件。
        //-f image2 表示输出格式为图像。
        //-ss 2 表示截取视频的第2秒的画面。
        //-s 700x525 表示生成图片的尺寸为 700x525。
        List commands = new ArrayList();
        commands.add(ffmpeg_path);
        commands.add("-i");
        commands.add(veido_path);
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-ss");
        commands.add("2");//这个参数是设置截取视频多少秒时的画面
        //commands.add("-t");
        //commands.add("0.001");
        commands.add("-s");
        commands.add("700x525");
//        commands.add("-vf");
//        commands.add("scale=320:-1");
        commands.add("-loglevel");
        commands.add("quiet"); // 设置日志级别为 quiet
        commands.add(picPath);
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.inheritIO(); // 将子进程的输入/输出流连接到 Java 进程的输入/输出流
        try {
            Process process = builder.start();
            process.waitFor(); // 等待子进程执行完毕
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("缩略图生成成功：" + picPath);
        return true;
    }

    @Test
    void ffmepg_db_test() {
        String videoPath = "C:\\Users\\27996\\Videos\\Captures\\Minecraft_ 1.16.5 - 单人游戏 2023-07-14 13-38-35.mp4";
        String Path ="ThumbnailPackage/" ;
        String thumbnailPath = Path+"11.png";
        File file = new File(Path);
        if (!file.exists()){
            if (file.mkdirs()) {
                System.out.println("ThumbnailPackage created successfully.");
            } else {
                System.err.println("Failed to create ThumbnailPackage.");
            }
        }
        String ffmpegPath = "E:/JavaStudySource/otherSoftware/ffmpeg-6.1.1-essentials_build/bin/ffmpeg.exe";
        if (generateThumbnail(videoPath, ffmpegPath,thumbnailPath)) {
            System.out.println("缩略图生成成功，并已存入数据库！");
        } else {
            System.out.println("缩略图生成失败！");
        }
    }
    public boolean generateThumbnail(String videoPath, String ffmpegPath,String picPath) {
        File file = new File(videoPath);
        if (!file.exists()) {
            System.err.println("路径[" + videoPath + "]对应的视频文件不存在!");
            return false;
        }
        //-i 参数后面跟着视频文件的路径。
        //-y 参数表示覆盖已存在的输出文件。
        //-f image2 表示输出格式为图像。
        //-ss 2 表示截取视频的第2秒的画面。
        //-s 700x525 表示生成图片的尺寸为 700x525。
        List commands = new ArrayList();
        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(videoPath);
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-ss");
        commands.add("2");//这个参数是设置截取视频多少秒时的画面
        //commands.add("-t");
        //commands.add("0.001");
        commands.add("-s");
        commands.add("700x525");
//        commands.add("-vf");
//        commands.add("scale=320:-1");
        commands.add("-loglevel");
        commands.add("info"); // 设置日志级别为 quiet
        commands.add(picPath);
        try {
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process process = builder.start();
//            // 读取缩略图的输出流
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            StringBuilder thumbnailBuilder = new StringBuilder();
//            while ((line = reader.readLine()) != null) {
//                thumbnailBuilder.append(line);
//            }
//// 读取缩略图的标准错误流
//            byte[] errorBytes = process.getErrorStream().readAllBytes();
//            String errorOutput = new String(errorBytes, StandardCharsets.UTF_8);
//            System.out.println("Error Output: " + errorOutput);
//            // 转换为 Base64 字符串
//            String base64Thumbnail = Base64.encodeBase64String(thumbnailBuilder.toString().getBytes(StandardCharsets.UTF_8));
//            //存入数据库
//            FileInfo fileInfo = new FileInfo();
//            fileInfo.setFileId("402cbaf4f1424eab9c2d3a94297d798a");
//            System.out.println(base64Thumbnail);
//            fileInfo.setFileCover(base64Thumbnail);
//            int count = fileInfoMapper.updateByPrimaryKeySelective(fileInfo);
//            if (count==0){
//                System.out.println("生成缩略图失败");
//                return false;
//            }
            System.out.println("生成缩略图成功");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
    }
    @Test
    void testImageFFmpeg(){
        String imagePath="C:\\Users\\27996\\Pictures\\Saved Pictures\\15c4130f8f47e22496a14715cd1a8b89_6499126140778385938.jpg";
        String picPath=DEFAULT_THUMBNAIL_PACKAGE+"01.png";
        boolean b = fuUtil.generateImageThumbnail(imagePath, picPath);
        if (b){
            System.out.println("生成成功");
        }else {
            System.out.println("失败");
        }
    }
    @Test
    void testdelFile(){
        String[] strings = FileAboutUtil.splitByLastDot("test.1.png");
        System.out.println(strings[0]);
        System.out.println(strings[1]);
    }
    @Test
    void testRedisRpush(){

        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId("4545451451");
        fileInfo.setUserId("454545145DWDADWD1");
        Boolean hasKey = stringRedisTemplate.hasKey("ces");
        if (!hasKey){
            stringRedisTemplate.opsForList().rightPush("ces",JSONUtil.toJsonStr(fileInfo));
            for (int i = 0; i < 10; i++) {
                Long ces = stringRedisTemplate.opsForList().rightPush("ces", String.valueOf(i));
                System.out.println(ces);
            }
        }
        List<String> cesList = stringRedisTemplate.opsForList().range("ces", 0, -1);

        int lastIndex = cesList.size()-1;
        String lastElement = cesList.get(lastIndex);
        System.out.println("最后一个数据的下标: " + lastIndex);
        System.out.println("最后一个数据的值: " + lastElement);
    }
    @Test
    void testGetFileAndChild(){
//111   88aa93ea32754c73a15b14046ec234e3  f85966923c7f4ffea07121ed2e9cb7c0
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey("88aa93ea32754c73a15b14046ec234e3", "f85966923c7f4ffea07121ed2e9cb7c0");
        List<FileInfo> files = new ArrayList<FileInfo>();
        files= fileInfoService.getFileAndChild(fileInfo, files);
        for (FileInfo info : files) {
            System.out.println(info.toString());
        }
    }
    @Test
    void test(){
        String messageTemplate="你好，您的邮箱验证码是：%s，15分钟有效";
        String message = String.format(messageTemplate, "qianqiu");
        System.out.println(message);
    }



}
