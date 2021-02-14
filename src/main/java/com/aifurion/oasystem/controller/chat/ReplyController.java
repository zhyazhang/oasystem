package com.aifurion.oasystem.controller.chat;

import com.aifurion.oasystem.common.enums.VoteEnum;
import com.aifurion.oasystem.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 15:44
 */


@Controller
public class ReplyController {

    @Autowired
    private ReplyService replyService;


    @PostMapping("/replyhandle")
    public String reply(HttpServletRequest req,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "5") int size,
                        @SessionAttribute("userId") Long userId, Model model) {

        replyService.reply(req, page, size, userId, model);

        return "chat/replytable";
    }


    @PostMapping("/likethis")
    public void likeThis(HttpServletRequest req, HttpServletResponse resp, Long userId) {
        replyService.likeThis(req, userId);

    }



    @PostMapping("/replypaging")
    public String replyPaging(HttpServletRequest req,
                              @RequestParam(value = "selecttype") Long selecttype,
                              @RequestParam(value = "selectsort") Long selectsort,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size,
                              @SessionAttribute("userId") Long userId, Model model) {


        replyService.replyPaging(req, selecttype, selectsort, page, size, userId, model);
        return "chat/replytable";

    }


    @PostMapping("/replydelete")
    public String replyDelete(HttpServletRequest req,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size,
                              @SessionAttribute("userId") Long userId, Model model) {

        replyService.replyDelete(req, page, size, userId, model);

        return "chat/replytable";

    }


    @PostMapping("/votehandle")
    public String voteHandle(HttpServletRequest req, @SessionAttribute("userId") Long userId, Model model) {

        VoteEnum voteEnum = replyService.voteHandle(req, userId, model);

        switch (voteEnum) {
            case NOTSTART:
                return "状态为未开始";
            case OVER:
                return "状态为已结束";
            case HAVEVOTE:
                return "你已经投过票了";
            default:
                return "chat/votetable";

        }


    }


    @PostMapping("/likeuserload")
    public String likeUserLoad(HttpServletRequest req, Model model, @SessionAttribute("userId") Long userId) {


        String module = req.getParameter("module");
        if ("discuss".equals(module)) {
            replyService.likeUserLoad(req, model, userId,module);
            return "chat/discusslike";
        } else if ("reply".equals(module)) {
            replyService.likeUserLoad(req, model, userId,module);
            return "chat/replylike";
        } else {

            return "参数异常";
        }

    }


}
