package com.qianqiu.clouddisk.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.utils.enums.FileCategoryEnums;
import com.qianqiu.clouddisk.utils.enums.FileTypeEnums;
import com.qianqiu.clouddisk.utils.enums.ShareValidTypeEnums;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_THUMBNAIL_PACKAGE;

@Slf4j
public class FileAboutUtil {
    private static final Map<String, FileTypeEnums> FILE_TYPE_MAP;

    static {
        Map<String, FileTypeEnums> tempMap = new HashMap<>();
        tempMap.put(".pdf", FileTypeEnums.PDF);
        tempMap.put(".doc", FileTypeEnums.DOC);
        tempMap.put(".docx", FileTypeEnums.DOC);
        tempMap.put(".xls", FileTypeEnums.EXCEL);
        tempMap.put(".xlsx", FileTypeEnums.EXCEL);
        tempMap.put(".xlsm", FileTypeEnums.EXCEL);
        tempMap.put(".txt", FileTypeEnums.TXT);
        tempMap.put(".md", FileTypeEnums.TXT);
        tempMap.put(".xml", FileTypeEnums.CODE); //XML文件
        tempMap.put(".java", FileTypeEnums.CODE); // Java源代码文件
        tempMap.put(".cpp", FileTypeEnums.CODE); // C++源代码文件
        tempMap.put(".c", FileTypeEnums.CODE); // C语言源代码文件
        tempMap.put(".py", FileTypeEnums.CODE); // Python脚本文件
        tempMap.put(".js", FileTypeEnums.CODE); // JavaScript脚本文件
        tempMap.put(".html", FileTypeEnums.CODE); // HTML网页文件
        tempMap.put(".css", FileTypeEnums.CODE); // CSS样式表文件
        tempMap.put(".php", FileTypeEnums.CODE); // PHP脚本文件
        tempMap.put(".rb", FileTypeEnums.CODE); // Ruby脚本文件
        tempMap.put(".swift", FileTypeEnums.CODE); // Swift源代码文件
        tempMap.put(".go", FileTypeEnums.CODE); // Go语言源代码文件
        tempMap.put(".ts", FileTypeEnums.CODE); // TypeScript源代码文件
        tempMap.put(".cs", FileTypeEnums.CODE); // C#源代码文件
        tempMap.put(".dart", FileTypeEnums.CODE); // Dart源代码文件
        tempMap.put(".lua", FileTypeEnums.CODE); // Lua脚本文件
        tempMap.put(".pl", FileTypeEnums.CODE); // Perl脚本文件
        tempMap.put(".sh", FileTypeEnums.CODE); // Shell脚本文件
        tempMap.put(".jsx", FileTypeEnums.CODE); // JSX文件（用于React.js开发）
        tempMap.put(".vue", FileTypeEnums.CODE); // Vue.js组件文件
        tempMap.put(".zip", FileTypeEnums.ZIP);
        tempMap.put(".7z", FileTypeEnums.ZIP);
        tempMap.put(".rar", FileTypeEnums.ZIP);
        tempMap.put(".tar", FileTypeEnums.ZIP);
        tempMap.put(".gz", FileTypeEnums.ZIP);
        FILE_TYPE_MAP = Collections.unmodifiableMap(tempMap);
    }

    /**
     * 获取匹配文件类型
     *
     * @param file
     * @return
     */
    public static FileCategoryEnums getFileCategoryType(MultipartFile file) {
        String contentType = file.getContentType();
        System.out.println(contentType + "---------------------------------------------------------------");
        if (StrUtil.isBlank(contentType)) {
            return null;
        }
        if (contentType.startsWith("application/zip") ||
                contentType.startsWith("application/x-7z-compressed") ||
                contentType.startsWith("application/x-rar-compressed") ||
                contentType.startsWith("application/x-tar")) {
            return FileCategoryEnums.OTHERS;
        }
        if (contentType.startsWith("application") || contentType.startsWith("text")) {
            return FileCategoryEnums.DOC;
        }
        if (contentType.startsWith("video")) {
            return FileCategoryEnums.VIDEO;
        }
        if (contentType.startsWith("audio")) {
            return FileCategoryEnums.AUDIO;
        }
        if (contentType.startsWith("image")) {
            return FileCategoryEnums.IMAGE;
        }
        return FileCategoryEnums.OTHERS;
    }

    public static FileCategoryEnums getFileCategoryType(String sourceFileType) {
        log.info("根据ContentType分配文件分类|参数|sourceFileType:{}", sourceFileType);
        String contentType = sourceFileType;
        if (StrUtil.isBlank(contentType)) {
            return null;
        }
        if (
                contentType.startsWith("application/x") ||
                        contentType.startsWith("application/octet-stream")) {
            log.info("压缩包文件|动画文件|可执行文件|镜像文件|未知文件，分类为OTHERS");
            return FileCategoryEnums.OTHERS;
        }
        if (contentType.startsWith("application") || contentType.startsWith("text")) {
            log.info("文档文件，分类为DOC");
            return FileCategoryEnums.DOC;
        }
        if (contentType.startsWith("video")) {
            log.info("视频文件，分类为VIDEO");
            return FileCategoryEnums.VIDEO;
        }
        if (contentType.startsWith("audio")) {
            log.info("音频文件，分类为AUDIO");
            return FileCategoryEnums.AUDIO;
        }
        if (contentType.startsWith("image")) {
            log.info("图片文件，分类为IMAGE");
            return FileCategoryEnums.IMAGE;
        }
        log.info("其他文件，分类为OTHERS");
        return FileCategoryEnums.OTHERS;
    }

    public static FileTypeEnums getFileTypeByCategory(FileCategoryEnums fileCategory, String fileSuffix) {
        log.info("根据分类和文件后缀细分文件类型|参数|fileCategory:{}|fileSuffix:{}", fileCategory, fileSuffix);
        Integer category = fileCategory.getCategory();
        //目录
        if (category == 0) {
            log.info("文件类型为目录|category:{}|fileSuffix:{}|FOLDER", category, fileSuffix);
            return FileTypeEnums.FOLDER;
        }
        //视频
        if (category == 1) {
            log.info("文件类型为视频|category:{}|fileSuffix:{}|VIDEO", category, fileSuffix);
            return FileTypeEnums.VIDEO;
        }
        //音频
        if (category == 2) {
            log.info("文件类型为音频|category:{}|fileSuffix:{}|AUDIO", category, fileSuffix);
            return FileTypeEnums.AUDIO;
        }
        //图片
        if (category == 3) {
            return FileTypeEnums.IMAGE;
        }
        //文档
        if (category == 4) {
            //根据后缀名判断
            String lowerCase = fileSuffix.toLowerCase();
            FileTypeEnums fileTypeEnums = FILE_TYPE_MAP.get(lowerCase);
            if (fileTypeEnums == null) {
                log.info("文件类型为文档中的其他文件|category:{}|fileSuffix:{}|OTHER", category, fileSuffix);
                return FileTypeEnums.OTHER;
            }
            log.info("文件类型为{}|category:{}|fileSuffix:{}|{}", fileTypeEnums.getTypeName(), category, fileSuffix, fileTypeEnums);
            return fileTypeEnums;
        }
        //其他category
        if (category == 5) {
            //根据后缀名判断
            String lowerCase = fileSuffix.toLowerCase();
            FileTypeEnums fileTypeEnums = FILE_TYPE_MAP.get(lowerCase);
            if (fileTypeEnums == null) {
                log.info("文件类型为其他中的其他文件|category:{}|fileSuffix:{}|OTHER", category, fileSuffix);
                return FileTypeEnums.OTHER;
            }
            log.info("文件类型为{}|category:{}|fileSuffix:{}|{}", fileTypeEnums.getTypeName(), category, fileSuffix, fileTypeEnums);
            return fileTypeEnums;
        }
        log.info("未知种类|分配OTHER");
        return FileTypeEnums.OTHER;
    }

    /**
     * 删除ThumbnailPackage零时文件
     *
     * @param thumbnailName
     * @return
     */
    public static boolean delThumbnailPackageFileByName(String thumbnailName) {
        log.info("删除临时缩略图中|参数|thumbnailName:{}", thumbnailName);
        File filePackage = new File(DEFAULT_THUMBNAIL_PACKAGE);
        //应该不用判断文件夹是否存在，只有缩略图生成成功后我才会调用
        File[] files = filePackage.listFiles();
        if (files == null) {
            //是空的？不好说什么原因，暂时先放着，应该不影响，直接返回
            return true;
        }
        for (File file : files) {
            if (file.getName().equals(thumbnailName)) {
                boolean delete = file.delete();
                if (!delete) {
                    log.info("文件删除失败，重试？");
                    return false;
                } else {
                    break;
                }
            } else {
                log.info("没有对应的文件");
                //不影响
                return true;
            }
        }
        return true;
    }

    /**
     * 获取文件md5
     *
     * @param file
     * @return
     */
    public static String getMD5(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // 使用 Hutool 提供的 FileUtil.md5Hex 方法计算 MD5 值
            return DigestUtil.md5Hex(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常，你可以根据实际情况返回默认值或抛出自定义异常
            return null;
        }
    }

    /**
     * 分割文件名
     *
     * @param input
     * @return
     */
    public static String[] splitByLastDot(String input) {
        // 找到最后一个点的索引
        int lastDotIndex = input.lastIndexOf(".");

        // 如果没有找到点，或者最后一个点在字符串的开头，则返回原始字符串和空字符串
        if (lastDotIndex == -1 || lastDotIndex == 0) {
            return new String[]{input, ""};
        }

        // 使用substring方法获取两个子字符串
        String part1 = input.substring(0, lastDotIndex);
        String part2 = input.substring(lastDotIndex);

        return new String[]{part1, part2};
    }

    /**
     * 将本地文件转为base64编码
     *
     * @param filePath
     * @return
     */
    public static String fileToBase64(String filePath) {
        try {
        log.info("生成文件base64编码|参数|filePath:{}", filePath);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            log.info("生成文件base64编码|指定的路径不是文件或文件不存在！");
            return null;
        }
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String fileBase64 = Base64.getEncoder().encodeToString(fileContent);
            log.info("文件base64编码生成成功|fileBase64:{}", fileBase64);
            return fileBase64;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("生成文件base64编码异常|异常{}", e);
            return null;
        }
    }

    /**
     * 更改文件后缀为png
     */
    public static String fileSuffixToPNG(String input) {
        String newStr = null;
        String[] strings = splitByLastDot(input);
        if (StrUtil.isBlank(strings[0])) {
            newStr = IdUtil.simpleUUID() + ".png";
            return newStr;
        }
        newStr = strings[0] + ".png";
        log.info("更改文件后缀名中|原:{}-->现:{}", input, newStr);
        return newStr;
    }

    public static FileCategoryEnums getCodeByCategory(Integer categoryCode) {
        if (categoryCode == 0) {
            return FileCategoryEnums.FOLDER;
        }
        if (categoryCode == 1) {
            return FileCategoryEnums.VIDEO;
        }
        if (categoryCode == 2) {
            return FileCategoryEnums.AUDIO;
        }
        if (categoryCode == 3) {
            return FileCategoryEnums.IMAGE;
        }
        if (categoryCode == 4) {
            return FileCategoryEnums.DOC;
        }
        return FileCategoryEnums.OTHERS;
    }

    public static FileCategoryEnums getCategoryByCategoryCode(String categoryCode) {
        if (FileCategoryEnums.FOLDER.getCode().equals(categoryCode)) {
            return FileCategoryEnums.FOLDER;
        }
        if (FileCategoryEnums.VIDEO.getCode().equals(categoryCode)) {
            return FileCategoryEnums.VIDEO;
        }
        if (FileCategoryEnums.AUDIO.getCode().equals(categoryCode)) {
            return FileCategoryEnums.AUDIO;
        }
        if (FileCategoryEnums.IMAGE.getCode().equals(categoryCode)) {
            return FileCategoryEnums.IMAGE;
        }
        if (FileCategoryEnums.DOC.getCode().equals(categoryCode)) {
            return FileCategoryEnums.DOC;
        }
        return FileCategoryEnums.OTHERS;
    }

    public static String fileReNameByAddNum(List<String> fileNameList, String refileName) {
        String[] strings = FileAboutUtil.splitByLastDot(refileName);
        String fileNamePrefix = strings[0];
        String fileNameSuffix = strings[1];
        int maxNumber = 0;
        String maxNumberFileName = fileNamePrefix;
        // 正则表达式模式，用于匹配括号内的数字
        Pattern pattern = Pattern.compile("\\((\\d+)\\)");
        // 遍历文件名列表，查找括号内的最大编号
        for (String fileName : fileNameList) {
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find()) {
                int number = Integer.parseInt(matcher.group(1));
                if (number > maxNumber) {
                    maxNumber = number;
                    maxNumberFileName = fileName;
                }
            }
        }
// 如果refileName不包含括号内编号，直接在文件名后面添加编号
        if (maxNumber == 0) {
            return fileNamePrefix + "(" + (maxNumber + 1) + ")" + fileNameSuffix;
        } else {
            strings = FileAboutUtil.splitByLastDot(maxNumberFileName);
            fileNamePrefix = strings[0];
            fileNameSuffix = strings[1];
            // 增加最大编号并更新新文件名
            fileNamePrefix = fileNamePrefix.replaceAll("\\(\\d+\\)", "(" + (maxNumber + 1) + ")");
            String newFileName = fileNamePrefix + fileNameSuffix;
            return newFileName;
        }
    }

    /**
     * 根据最后一个 / 分割字符串
     *
     * @param input
     * @return
     */
    public static String[] splitString(String input) {
        int index = input.lastIndexOf("/");
        if (index != -1) {
            String firstPart = input.substring(0, index + 1);
            String secondPart = input.substring(index + 1);
            return new String[]{firstPart, secondPart};
        } else {
            // 如果没有找到斜杠，则返回原始字符串作为第一部分，第二部分为空字符串
            return new String[]{input, ""};
        }
    }

    public static ShareValidTypeEnums getShareValidType(Integer type) {
        if (ShareValidTypeEnums.DAY_1.getType().equals(type)) {
            return ShareValidTypeEnums.DAY_1;
        }
        if (ShareValidTypeEnums.DAY_7.getType().equals(type)) {
            return ShareValidTypeEnums.DAY_7;
        }
        if (ShareValidTypeEnums.DAY_30.getType().equals(type)) {
            return ShareValidTypeEnums.DAY_30;
        }
        return ShareValidTypeEnums.FOREVER;
    }
    public static boolean getDocx(String previewUrl, HttpServletResponse response, FileInfo fileInfo){
        return getUrlToBlob(previewUrl,response,fileInfo);
    }
    public static boolean getTxtAndCode(String previewUrl, HttpServletResponse response,FileInfo fileInfo){
        return getUrlToBlob(previewUrl,response,fileInfo);
    }

    public static  boolean getUrlToBlob(String previewUrl, HttpServletResponse response,FileInfo fileInfo) {
        try {
            // 从 URL 获取文件字节流 ,进行encodeURI格式的编码处理
            String encodePreviewUrl = previewUrl;
            URL url = new URL(encodePreviewUrl);
            URLConnection conn = url.openConnection();

            // 设置响应内容类型
            response.setContentType(conn.getContentType());

            // 设置文件名
            String fileName = fileInfo.getFileName();
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);

            // 获取 URL 输入流
            InputStream inputStream = conn.getInputStream();

            // 获取响应输出流
            OutputStream outputStream = response.getOutputStream();

            // 将文件内容复制到响应输出流
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // 关闭流
            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean getExcel(String previewUrl, HttpServletResponse response, FileInfo fileInfo) {
        try {
            // 创建URL对象
            String encodePreviewUrl = previewUrl;
            URL url = new URL(encodePreviewUrl);
            // 打开连接
            URLConnection connection = url.openConnection();
            // 获取输入流
            InputStream inputStream = connection.getInputStream();
            // 读取数据
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            // 将读取的数据转换为字节数组
            byte[] bytes = byteArrayOutputStream.toByteArray();
            // 关闭流
            bufferedInputStream.close();
            byteArrayOutputStream.close();
            inputStream.close();

            // 将字节数组写入 HttpServletResponse 并响应给前端
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileInfo.getFileName() + "\"");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean isExpiredFile(Date recoveryTime) {
        Date currentTime = DateUtil.date();
        // 计算时间差
        long betweenDays = DateUtil.betweenDay(recoveryTime, currentTime, true);
        System.out.println(betweenDays);
        // 判断时间差是否超过30天
        return betweenDays > 30;
    }

}
