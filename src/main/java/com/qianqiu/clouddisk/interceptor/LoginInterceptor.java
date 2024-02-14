package com.qianqiu.clouddisk.interceptor;

import com.qianqiu.clouddisk.model.vo.UserInfoVo;
import com.qianqiu.clouddisk.utils.UserThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
         //1.判断是否需要拦截(ThreadLocal中是否有用户)
        UserInfoVo user = UserThreadLocal.getUser();
        if (user==null) {
            //没有？，拦截
            response.setStatus(401);
            return false;
        }
        //有用户，则放行
        return true;
    }

}
