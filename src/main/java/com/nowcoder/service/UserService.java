package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;


@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String username, String password){
        Map<String,Object> map=new HashMap<String,Object>();
        if (StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }
        User user=userDAO.selectByName(username);
        if(user!=null){
            map.put("msgname","用户名已被注册");
            return map;
        }
        //密码强度
        user=new User();
        user.setName(username);
        //用UUID，随机产生5个数字
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        /**
         * 这种是完全不行的，直接在数据库里传的是明文，信息可能泄露。
         */
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        //user.setPassword(password);
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
        //写入数据库了
        userDAO.addUser(user);
        //登录
        String loginTicket=addLoginTicket(user.getId());
        map.put("ticket",loginTicket);

        return map;
    }
    public Map<String, Object> login(String username, String password){
        Map<String,Object> map=new HashMap<String,Object>();
        if (StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user==null){
            map.put("msgname","用户名不存在");
            return map;
        }
        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd","密码不正确");
            return map;
        }
        map.put("userId", user.getId());

        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }
    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        //0代表登录
        ticket.setStatus(0);
        //设置ticket
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }
    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
        //loginTicketDAO.updateTicket(ticket,"xuxinghua");
    }

}
