package com.qianqiu.clouddisk.utils;

import com.qianqiu.clouddisk.acpect.EnableCheckAspect;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_THUMBNAIL_PACKAGE;
import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_THUMBNAIL_SIZE;
@Slf4j
@Component
public class FFmepgUtil {
    private static Logger logger = LoggerFactory.getLogger(FFmepgUtil.class);
    @Value("${ffmpegPath}")
    private String ffmpegPath;

    public FFmepgUtil() {
        createThumbnailPackage();
    }

    private void createThumbnailPackage(){
        File file = new File(DEFAULT_THUMBNAIL_PACKAGE);
        if (!file.exists()){
            if (file.mkdirs()) {
                logger.info("临时缩略图文件夹已经生成");
            } else {
                logger.info("临时缩略图文件夹生成失败");
            }
        }
    }

    /**
     * 截取视频的一帧作为分面
     * @param videoPath
     * @param picPath
     * @return
     */
    public boolean generateVideoThumbnail(String videoPath, String picPath) {
        log.info("生成视频缩略图中|参数|videoPath:{}|picPath:{}",videoPath,picPath);
        //只能访问本地文件
//        File file = new File(videoPath);
//        if (!file.exists()) {
//            System.err.println("路径[" + videoPath + "]对应的视频文件不存在!");
//            return false;
//        }
        //-i 参数后面跟着视频文件的路径。
        //-y 参数表示覆盖已存在的输出文件。
        //-f image2 表示输出格式为图像。
        //-ss 2 表示截取视频的第2秒的画面。
        //-s 700x525 表示生成图片的尺寸为 700x525。
        picPath=DEFAULT_THUMBNAIL_PACKAGE+picPath;
        List commands = new ArrayList();
        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(videoPath);
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-ss");
        commands.add("2");//这个参数是设置截取视频多少秒时的画面
        commands.add("-s");
        commands.add(DEFAULT_THUMBNAIL_SIZE);
        commands.add("-loglevel");
        commands.add("quiet"); // 设置日志级别为 quiet
        commands.add(picPath);
        try {
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process process = builder.start();
            process.waitFor();
            // 转换为 Base64 字符串
            log.info("生成缩略图成功,临时缩略图路径:{}",picPath);
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 缩放图片
     * @param imagePath
     * @param picPath
     * @return
     */
    public boolean generateImageThumbnail(String imagePath, String picPath){
//        File file = new File(imagePath);
//        if (!file.exists()) {
//            System.err.println("路径[" + imagePath + "]对应的图片文件不存在!");
//            return false;
//        }
        //-i 参数后面跟着视频文件的路径。
        //-y 参数表示覆盖已存在的输出文件。
        //-f image2 表示输出格式为图像。
        //-ss 2 表示截取视频的第2秒的画面。
        //-s 700x525 表示生成图片的尺寸为 700x525。
        List commands = new ArrayList();
        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(imagePath);
        commands.add("-y");
        commands.add("-vf");
//        暂时是缩放为20%
        commands.add("scale=iw*0.2:ih*0.2");
        commands.add("-loglevel");
        commands.add("quiet"); // 设置日志级别为 quiet
        commands.add(picPath);
        try {
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process process = builder.start();
            // 等待进程执行完成
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("调整图片大小成功");
                return true;
            } else {
                System.err.println("调整图片大小失败，FFmpeg 进程退出码: " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean isFileAccessible(String filePath) {
        try {
            URL url = new URL(filePath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
