package com.aifurion.oasystem.common.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/12 9:10
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private RecordInterceptor recordInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(recordInterceptor)

                //拦截所有请求
                .addPathPatterns("/**")
                //放行静态资源
                .excludePathPatterns("/bootstrap/**")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/easyui/**")
                .excludePathPatterns("/images/**")
                .excludePathPatterns("/js/**")

                //放行特殊请求
                .excludePathPatterns("/logins")
                .excludePathPatterns("/captcha")
                .excludePathPatterns("/test");


    }


}
