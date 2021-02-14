package com.aifurion.oasystem.controller.login;

import com.aifurion.oasystem.common.enums.LoginStateEnum;
import com.aifurion.oasystem.common.enums.UtilEnum;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.CaptchaService;
import com.aifurion.oasystem.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/11 21:17
 */
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private CaptchaService captchaService;

    private String captcha = UtilEnum.CAPTCHA_KEY.getContent();

    @GetMapping("/logins")
    public String login() {
        return "login/login";
    }

    @PostMapping("/logins")
    public String loginCheck(HttpSession session,HttpServletRequest request, Model model,
                             @RequestParam("userName") String userName,
                             @RequestParam("password") String password
                             /*@RequestParam("code") String code*/) {

        String sessionCode = (String) request.getSession().getAttribute(captcha);
        model.addAttribute("userName", userName);
        LoginStateEnum stateEnum = loginService.loginCheck(request, userName, password, "code");
        switch (stateEnum) {
            case codeError:
                model.addAttribute("errormess", "验证码输入错误！");
                break;
            case notMatch:
                model.addAttribute("errormess", "帐号或密码错误");
                break;
            case userLocked:
                model.addAttribute("errormess", "帐号已被锁定");
                break;
            case hasLogin:
                model.addAttribute("hasmess", "当前用户已经登录，不能重复登录");
                break;
            case ok:
                return "redirect:/index";
            default:
                break;
        }
        return "login/login";
    }



    @GetMapping("/loginout")
    public String loginOut(HttpSession session) {
        session.removeAttribute("userId");
        return "redirect:/logins";
    }


    @GetMapping("/handlehas")
    public String handleHas(HttpSession session) {

        if (!StringUtils.isEmpty(session.getAttribute("thisuser"))) {

            User user = (User) session.getAttribute("thisuser");
            session.removeAttribute("userId");
            session.setAttribute("userId", user.getUserId());
        } else {

            return "login/login";

        }
        return "redirect:/index";
    }

    @GetMapping("/captcha")
    public void captcha(HttpServletResponse response, HttpSession session) {
        captchaService.captcha(response, session);

    }


}
