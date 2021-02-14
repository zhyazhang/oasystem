package com.aifurion.oasystem.controller.index;

import com.aifurion.oasystem.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 10:57
 */

@Controller
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/index")
    public String index(HttpServletRequest request, Model model) {

        if (indexService.initIndex(request, model)) {

            return "login/login";
        } else {
            return "index/index";
        }
    }


    @GetMapping("/content")
    public String content(HttpServletRequest request, Model model) {

        indexService.initContent(request, model);

        return "systemcontrol/control";

    }



}
