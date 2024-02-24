package com.qianqiu.clouddisk.test;

import cn.hutool.core.date.DateUtil;
import com.qianqiu.clouddisk.utils.FileAboutUtil;
import com.qianqiu.clouddisk.utils.enums.FileCategoryEnums;
import com.qianqiu.clouddisk.utils.enums.FileTypeEnums;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qianqiu.clouddisk.utils.Constant.DefaultConstant.DEFAULT_THUMBNAIL_PACKAGE;

@SpringBootTest
public class FileAboutTest {
    @Test
    void test01(){
        String picPath="C:\\Users\\27996\\Desktop\\tttt\\1.png";
        String s = FileAboutUtil.fileToBase64(picPath);
        System.out.println(s);
    }
    @Test
    void  test02(){
        String  delPath="Minecraft_ 1.16.5 - 单人游戏 2023-07-01 22-54-31.mp4.png";
        boolean b = FileAboutUtil.delThumbnailPackageFileByName(delPath);
        System.out.println(b);
    }
    @Test
    void test03(){
        FileTypeEnums fileTypeByCategory = FileAboutUtil.getFileTypeByCategory(FileCategoryEnums.DOC, "");
        System.out.println(fileTypeByCategory.getTypeName());
        System.out.println(fileTypeByCategory.getTypeCode());
    }
    @Test
    void test04(){
        String[] strings = FileAboutUtil.splitByLastDot("54555");
        System.out.println(strings.length);
        System.out.println(strings[0].toString());
        System.out.println(strings[1].toString());
        System.out.println(strings.toString());
    }
    @Test
    void test05(){
        String s = FileAboutUtil.fileSuffixToPNG("156565.cg");
        System.out.println(s);

    }
    @Test
    void test06() {
        String s = "file";
        String s1 = "file(1)";
        String s2="file(3)";
        List<String> list=new ArrayList<>();
        list.add(s);
        list.add(s1);
        String str = FileAboutUtil.fileReNameByAddNum(list, "file");
        System.out.println(str);
    }
    @Test
    void test07(){
        // 假设参数日期为 2023-01-01
        String dateString = "2024-01-01";
        // 将参数日期解析为 Date 对象
        Date targetDate = DateUtil.parseDate(dateString);
        // 判断当前日期是否在参数日期之前
        boolean expiredFile = FileAboutUtil.isExpiredFile(targetDate);
        System.out.println("是否超过30天"+expiredFile);
    }
}
