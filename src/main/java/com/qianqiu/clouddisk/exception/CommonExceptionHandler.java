package com.qianqiu.clouddisk.exception;
import com.qianqiu.clouddisk.utils.commonResult.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;


/**
 * 统一异常处理
 */
@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult<Object> exceptionHandler(Exception e) {
        if (e instanceof AsyncRequestTimeoutException || e instanceof ClientAbortException) {
        } else {
            log.error(e.getMessage(), e);
        }
        return CommonResult.failed(e.getMessage());
    }


    @ExceptionHandler(CommonException.class)
    @ResponseBody
    public CommonResult<Object> exceptionHandler(CommonException e) {
        if (e.getCode() == 200) {
            return CommonResult.success(e.getData());
        }
        return CommonResult.failed(e.getMsg());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public CommonResult<Object> exceptionHandler(MissingServletRequestParameterException e) {
        return CommonResult.validateFailed(String.format("缺少参数%s", e.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public CommonResult<Object> exceptionHandler(MethodArgumentNotValidException e) {
        return CommonResult.validateFailed(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public CommonResult<Object> exceptionHandler(BindException e) {
        return CommonResult.validateFailed(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
}
