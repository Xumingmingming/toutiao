package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.News;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;

 @RequestMapping(value = "/reg/", method = {RequestMethod.GET, RequestMethod.POST})
 @ResponseBody
 //从请求body中获取参数
 public String reg(@RequestParam(value = "username") String username,
                   @RequestParam(value = "password") String password,
                   @RequestParam(value = "rember",defaultValue ="0") int rememberme,
                   HttpServletResponse response){
     try {
         Map<String, Object> map = userService.register(username, password);
         if (map.containsKey("ticket")) {
             Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
             /**
              * ???
              */
             cookie.setPath("/");
             if (rememberme > 0) {
                 cookie.setMaxAge(3600*24*5);
             }
             response.addCookie(cookie);
             return ToutiaoUtil.getJSONString(0, "注册成功");
         } else {
             return ToutiaoUtil.getJSONString(1, map);
         }

     } catch (Exception e) {
         logger.error("注册异常" + e.getMessage());
         return ToutiaoUtil.getJSONString(1, "注册异常");
     }

     }
    @RequestMapping(value = "/login/", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam(value = "username") String username,
                      @RequestParam(value = "password") String password,
                      @RequestParam(value = "rember",defaultValue ="0") int rememberme,
                        HttpServletResponse response){
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                //handler实现
                eventProducer.fireEvent(new EventModel(EventType.LOGIN).setActorId((int)map.get("userId")).
                        setExt("username","牛客").setExt("email", "xuxinghua1015@163.com"));
                return ToutiaoUtil.getJSONString(0, "注册成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }

    }
    @RequestMapping(value = "/logout/", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket){
     userService.logout(ticket);
     return "redirect:/";
    }

 }
