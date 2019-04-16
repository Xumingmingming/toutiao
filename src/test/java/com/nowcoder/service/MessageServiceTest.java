package com.nowcoder.service;

import com.nowcoder.ToutiaoApplication;
import com.nowcoder.model.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class MessageServiceTest {

    @Autowired MessageService messageService;

    @Test
    public void testMessageService(){

        Message message=new Message();
        message.setContent("I love you");
        message.setFromId(1);
        message.setToId(2);
        message.setCreatedDate(new Date());
        System.out.println(messageService.addMessage(message));
    }

}