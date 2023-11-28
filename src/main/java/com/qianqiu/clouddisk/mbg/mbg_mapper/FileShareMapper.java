package com.qianqiu.clouddisk.mbg.mbg_mapper;

import com.qianqiu.clouddisk.mbg.mbg_model.FileShare;
import com.qianqiu.clouddisk.mbg.mbg_model.FileShareExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FileShareMapper {
    long countByExample(FileShareExample example);

    int deleteByExample(FileShareExample example);

    int deleteByPrimaryKey(String shareId);

    int insert(FileShare row);

    int insertSelective(FileShare row);

    List<FileShare> selectByExample(FileShareExample example);

    FileShare selectByPrimaryKey(String shareId);

    int updateByExampleSelective(@Param("row") FileShare row, @Param("example") FileShareExample example);

    int updateByExample(@Param("row") FileShare row, @Param("example") FileShareExample example);

    int updateByPrimaryKeySelective(FileShare row);

    int updateByPrimaryKey(FileShare row);
}