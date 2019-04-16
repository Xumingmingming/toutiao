package com.nowcoder.controller;

import com.nowcoder.model.User;
import com.nowcoder.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {
    @Autowired
    private ToutiaoService toutiaoService;
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(path = {"/","/index"})
    @ResponseBody
    public String index(HttpSession session){
        logger.info("Visit Index");
        return "woaini "+session.getAttribute("msg")+"<br>"
                +"Say:"+ toutiaoService.say();
    }
    //{}表示占位符
    @RequestMapping(value = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable( "groupId") String groupId,
                          @PathVariable("userId") int userId ,
                          @RequestParam(value = "type",defaultValue = "1") int type,
                          @RequestParam(value = "key",defaultValue = "nowcode") String key){
      return String.format("GID{%s},UID{%d},TYPE{%d},KEY{%s}",groupId,userId,type,key);

    }
//在版本较新的springboot中找不见templates中的.vm文件
    @RequestMapping(value = {"/vm"})
    //没有@ResponseBody，返回的是resources/templates下的文件
    public String news(Model model){
        //这里注意：有个addAttribute（)方法和addAttributes()方法
     model.addAttribute("value1","vv1");
        List<String> colors= Arrays.asList(new String[]{"red","green","yellow"});
        Map<String,String> map=new HashMap<String,String>();
        for(int i=0;i<4;i++){
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute("colors",colors);
        model.addAttribute("map",map);
        model.addAttribute("user",new User("xuxinghua "));
        return "news";
    }

       @RequestMapping(value = "/request")
       @ResponseBody
       public String request(HttpServletRequest request,
                             HttpServletResponse response,
                             HttpSession session){
            StringBuilder sb=new StringBuilder();
           Enumeration<String> headerNames=request.getHeaderNames();
           while (headerNames.hasMoreElements()){
               String name=headerNames.nextElement();
               sb.append(name+":"+request.getHeader(name)+"<br>");


           }

           for (Cookie cookie:request.getCookies()){
               sb.append("Cookie:");
               sb.append(cookie.getName());
               sb.append(":");
               sb.append(cookie.getValue());
               sb.append("<br>");
           }
           sb.append("getMethod:" + request.getMethod() + "<br>");
           sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
           sb.append("getQueryString:" + request.getQueryString() + "<br>");
           sb.append("getRequestURI:" + request.getRequestURI() + "<br>");
           return sb.toString();
       }
    @RequestMapping(value = {"/response"})
    @ResponseBody
       public String response(@CookieValue(value = "nowcodeid",defaultValue = "a") String nowcodeid,
                              @RequestParam(value = "key",defaultValue = "key") String key,
                              @RequestParam(value = "value",defaultValue = "value") String value,
                              HttpServletResponse response){
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return  "NowCoderId From Cookie:" + nowcodeid;
    }

    @RequestMapping("/redirect/{code}")
//    public RedirectView redirect(@PathVariable("code") int code){
//        RedirectView red =new RedirectView("/",true);
//        if (code==301){
//            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//        }
//        return red;
//    }
    public String redirect(@PathVariable("code") int code,
                           HttpSession session){
        session.setAttribute("msg","Jump from redirect!");
        return "redirect:/";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key",required = false) String key){
        if ("admin".equals(key)){
            return "Hello admin";

        }throw new IllegalArgumentException("Key error");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return  "error"+e.getMessage();
    }
}

