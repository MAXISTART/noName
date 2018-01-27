package com.school.store.interceptor;

import com.school.store.admin.admin.entity.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MyInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(MyInterceptor.class);

    /**
     *   这个是拦截登录得主要方法
     * @param request
     * @param response
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        logger.info(" in prehandle");

        //获取session
        HttpSession session = request.getSession(true);

        // 这里仅仅是用来测试的session用的
        if(session.getAttribute("admin") == null){
            Admin admin = new Admin();
            admin.setId("1");
            admin.setName("maxistar");
            admin.setPhoneNumber("15603004668");
            session.setAttribute("admin", admin);
        }


        //判断用户ID是否存在，不存在就跳转到登录界面
/*        if(session.getAttribute("userId") == null){
            logger.info("------:跳转到login页面！");
            logger.info("转发到： " + request.getContextPath()+"/user/turn_login_page");
            response.sendRedirect(request.getContextPath()+"/user/turn_login_page");
            // 下面的return false是一定要的，说明拦截器还会继续拦截，不放行请求
            return false;
        }else{
            session.setAttribute("userId", session.getAttribute("userId"));
            return true;
        }*/

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
