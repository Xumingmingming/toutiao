package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * ThreadLocal的用法
 *
 * 使用ThreadLocal把里面的值共享给所有的类
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();;
    }
}
