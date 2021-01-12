package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.enums.UtilEnum;
import com.aifurion.oasystem.common.tool.VerifyCodeUtils;
import com.aifurion.oasystem.service.CaptchaService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/12 17:12
 */

@Service
public class CaptchaServiceImpl implements CaptchaService {
    @Override
    public void captcha(HttpServletResponse response, HttpSession session) {

        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);

        int w = 135, h = 40;

        try {

            VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);

        } catch (IOException e) {
            e.printStackTrace();
        }
        session.setAttribute(UtilEnum.CAPTCHA_KEY.getContent(), verifyCode.toLowerCase());

    }
}
