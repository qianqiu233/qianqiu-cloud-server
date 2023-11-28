package com.qianqiu.clouddisk.exception;

import com.qianqiu.clouddisk.utils.commonResult.ResultCode;
import org.springframework.data.annotation.Transient;


/**
 * 通用异常处理类
 */
public class CommonException extends RuntimeException {
	private final long code;
    private final String msg;

    @Transient
    //不进行持久化，不进入数据库
    private final Object data;

    public CommonException(long code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public CommonException(ResultCode type, Object data) {
        this.code = type.getCode();
        this.msg = type.getMsg();
        this.data = data;
    }

    public CommonException(ResultCode type) {
        this.code = type.getCode();
        this.msg = type.getMsg();
        this.data = null;
    }

    public CommonException(String msg) {
        this.code = 500;
        this.msg = msg;
        this.data = null;
    }

    public long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

}
