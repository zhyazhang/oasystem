package com.aifurion.oasystem.service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/12 17:11
 */
public interface CaptchaService {

    void captcha(HttpServletResponse response, HttpSession session);


}
