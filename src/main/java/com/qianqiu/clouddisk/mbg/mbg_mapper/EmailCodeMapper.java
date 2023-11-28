package com.qianqiu.clouddisk.mbg.mbg_mapper;

import com.qianqiu.clouddisk.mbg.mbg_model.EmailCode;
import com.qianqiu.clouddisk.mbg.mbg_model.EmailCodeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmailCodeMapper {
    long countByExample(EmailCodeExample example);

    int deleteByExample(EmailCodeExample example);

    int deleteByPrimaryKey(@Param("email") String email, @Param("code") String code);

    int insert(EmailCode row);

    int insertSelective(EmailCode row);

    List<EmailCode> selectByExample(EmailCodeExample example);

    EmailCode selectByPrimaryKey(@Param("email") String email, @Param("code") String code);

    int updateByExampleSelective(@Param("row") EmailCode row, @Param("example") EmailCodeExample example);

    int updateByExample(@Param("row") EmailCode row, @Param("example") EmailCodeExample example);

    int updateByPrimaryKeySelective(EmailCode row);

    int updateByPrimaryKey(EmailCode row);
}