package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.service.MessageService;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    MailSender mailSender;
    @Override
    public void doHandle(EventModel eventModel) {
        Message message=new Message();
        message.setCreatedDate(new Date());
        //加入系统默认ID是3
        message.setFromId(3);
        message.setToId(eventModel.getActorId());
        message.setContent("你上次的登陆IP异常");
        messageService.addMessage(message);

        Map<String, Object> map = new HashMap();
        map.put("username", eventModel.getExt("username"));
        mailSender.sendWithHTMLTemplate(eventModel.getExt("email"),"登录异常",
                "mails/welcome.html",map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
