package com.qianqiu.clouddisk.test;

import com.qianqiu.clouddisk.utils.FileAboutUtil;
import com.qianqiu.clouddisk.utils.enums.FileCategoryEnums;
import com.qianqiu.clouddisk.utils.enums.FileTypeEnums;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

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
}
