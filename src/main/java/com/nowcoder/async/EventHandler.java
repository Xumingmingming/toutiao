package com.nowcoder.async;

import java.util.List;

public interface EventHandler {
    void doHandle(EventModel eventModel);
    //可以帮助做哪些事件
    List<EventType> getSupportEventTypes();
}
