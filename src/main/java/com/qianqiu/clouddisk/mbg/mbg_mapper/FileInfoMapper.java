package com.qianqiu.clouddisk.mbg.mbg_mapper;

import com.qianqiu.clouddisk.mbg.mbg_model.FileInfo;
import com.qianqiu.clouddisk.mbg.mbg_model.FileInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FileInfoMapper {
    long countByExample(FileInfoExample example);

    int deleteByExample(FileInfoExample example);

    int deleteByPrimaryKey(@Param("fileId") String fileId, @Param("userId") String userId);

    int insert(FileInfo row);

    int insertSelective(FileInfo row);

    List<FileInfo> selectByExampleWithBLOBs(FileInfoExample example);

    List<FileInfo> selectByExample(FileInfoExample example);

    FileInfo selectByPrimaryKey(@Param("fileId") String fileId, @Param("userId") String userId);

    int updateByExampleSelective(@Param("row") FileInfo row, @Param("example") FileInfoExample example);

    int updateByExampleWithBLOBs(@Param("row") FileInfo row, @Param("example") FileInfoExample example);

    int updateByExample(@Param("row") FileInfo row, @Param("example") FileInfoExample example);

    int updateByPrimaryKeySelective(FileInfo row);

    int updateByPrimaryKeyWithBLOBs(FileInfo row);

    int updateByPrimaryKey(FileInfo row);
}