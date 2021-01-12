package com.aifurion.oasystem.common.tool;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/12 9:16
 */
public class Tool {

    public static <T> T getBean(Class<T> clazz, HttpServletRequest request) {

        WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        return webApplicationContext.getBean(clazz);


    }


}
