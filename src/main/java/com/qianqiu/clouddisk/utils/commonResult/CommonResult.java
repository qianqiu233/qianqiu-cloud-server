package com.qianqiu.clouddisk.utils.commonResult;

/**
 * 通用返回数据
 * @param <T>
 */
public class CommonResult <T>{
    //返回的状态码
    private long code;
    //返回的信息
    private String msg;
    //返回的数据
    private T data;
    public CommonResult() {}
    public CommonResult(long code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功 200
     * 返回结果数据 data
     * @param data
     * @return
     * @param <T>
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /**
     * 成功 200
     * 返回结果数据+提示信息 data+msg
     * @param data
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> CommonResult<T> success(T data,String msg) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败 500
     * 返回错误码 code
     * @param errorCode
     * @return
     * @param <T>
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode) {
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMsg(), null);
    }

    /**
     * 失败 500
     * 返回提示信息 msg
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> CommonResult<T> failed(String msg) {
        return new CommonResult<T>(ResultCode.FAILED.getCode(), msg, null);
    }

    /**
     * 失败 500
     * 直接放回失败
     * @return
     * @param <T>
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 失败 404
     * 参数校验失败
     * @return
     * @param <T>
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 失败 404
     * 参数校验失败返回提示信息 msg
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> CommonResult<T> validateFailed(String msg) {
        return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(), msg, null);
    }

    /**
     * 失败 401
     * 返回未登录结果 data
     * @param data
     * @return
     * @param <T>
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMsg(), data);
    }

    /**
     * 失败 403
     * 返回未授权结果 data
     * @param data
     * @return
     * @param <T>
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMsg(), data);
    }
    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
