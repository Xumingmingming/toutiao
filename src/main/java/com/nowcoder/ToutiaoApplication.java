package com.nowcoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

//启动自动配置：只能扫描与本类同级或者以下的包中的类
@SpringBootApplication
public class ToutiaoApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ToutiaoApplication.class);
	}

	//启动springboot程序，启动spring容器，启动内嵌的Tomcat（如果是web项目）
	public static void main(String[] args) {
		SpringApplication.run(ToutiaoApplication.class, args);
	}
}
