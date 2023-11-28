package com.qianqiu.clouddisk.mbg.mbg_mapper;

import com.qianqiu.clouddisk.mbg.mbg_model.UserInfo;
import com.qianqiu.clouddisk.mbg.mbg_model.UserInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper {
    long countByExample(UserInfoExample example);

    int deleteByExample(UserInfoExample example);

    int deleteByPrimaryKey(String userId);

    int insert(UserInfo row);

    int insertSelective(UserInfo row);

    List<UserInfo> selectByExample(UserInfoExample example);

    UserInfo selectByPrimaryKey(String userId);

    int updateByExampleSelective(@Param("row") UserInfo row, @Param("example") UserInfoExample example);

    int updateByExample(@Param("row") UserInfo row, @Param("example") UserInfoExample example);

    int updateByPrimaryKeySelective(UserInfo row);

    int updateByPrimaryKey(UserInfo row);
}