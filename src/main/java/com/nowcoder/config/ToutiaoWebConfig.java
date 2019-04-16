package com.nowcoder.config;

import com.nowcoder.interceptor.LoginRequiredInterceptor;
import com.nowcoder.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
@Component
//加入拦截器
public class ToutiaoWebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private PassportInterceptor passportInterceptor;
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 错误写法，要体现spring的IOC
         */
        //registry.addInterceptor(new PassportInterceptor());
        /**
         * 正确写法,通过自动配置
         */
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");
    }
}
