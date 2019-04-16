package com.nowcoder.interceptor;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginTicketDAO loginTicketDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String ticket=null;
        if (httpServletRequest.getCookies()!=null){
            for (Cookie cookie:httpServletRequest.getCookies()){
               if (cookie.getName().equals("ticket")){
                   ticket=cookie.getValue();
                   break;
               }
            }
        }
        //cookie可以伪造，所以光查看cookie不够
        //如果客户端有ticket存在，查看与服务器后端的数据库中的值是否相等
        if (ticket!=null){
            LoginTicket loginTicket=loginTicketDAO.selectByTicket(ticket);
            //ticket不相等、超过期限
            //这里感觉有问题
            if (loginTicket==null||loginTicket.getExpired().before(new Date())||loginTicket.getStatus()!=0)
            {
                return  true;
            }
            //认证过后，将用户信息保存在hostHolder中，方便后面的流程使用
            User user=userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
