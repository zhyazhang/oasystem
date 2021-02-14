package com.aifurion.oasystem.controller.chat;

import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.entity.discuss.Discuss;
import com.aifurion.oasystem.entity.discuss.VoteList;
import com.aifurion.oasystem.service.DiscussManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 11:51
 */

@Controller
public class ChatManageController {


    @Autowired
    private DiscussManageService discussManageService;


    @GetMapping("/chatlist")
    public String getChatList(@RequestParam(value = "page", defaultValue = "0") int page, Model model, HttpSession session) {

        discussManageService.getChatList(page, model, session);
        return "chat/chatmanage";
    }


    @GetMapping("/adminmanage")
    public String adminManage(@RequestParam(value = "page", defaultValue = "0") int page, HttpSession session,
                              @SessionAttribute("userId") Long userId, Model model) {

        discussManageService.adminManage(page, session, userId, model);

        return "chat/chatmanage";
    }


    @RequestMapping("/chatmanage")
    public String chatManage(@RequestParam(value = "page", defaultValue = "0") int page,
                             @SessionAttribute("userId") Long userId, Model model, HttpSession session) {


        discussManageService.chatManage(page, userId, model, session);
        return "chat/chatmanage";
    }


    @GetMapping("/deletediscuss")
    public String deleteDiscuss(@RequestParam("name") String name, @SessionAttribute("userId") Long userId,
                                @RequestParam("discussId") Long discussId, @RequestParam("page") String page) {


        boolean b = discussManageService.deleteDiscuss(name, userId, discussId);

        if (b) {

            if ("超级管理员".equals(name)) {
                return "forward:/adminmanage?page=" + page;
            } else if ("我的管理".equals(name)) {
                return "forward:/chatmanage?page=" + page;
            } else{
                return "forward:/chatlist?page=" + page;
            }
        } else {
            return "redirect:/notlimit";
        }

    }



    @GetMapping("/chattable")
    public String chatTable(@RequestParam(value="page",defaultValue="0") int page,
			@RequestParam(value="baseKey",required=false) String baseKey,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="time",required=false) String time,
			@RequestParam(value="visitnum",required=false) String visitnum,
			@RequestParam(value="icon",required=false) String icon,
			@SessionAttribute("userId") Long userId,Model model) {
        discussManageService.chatTable(page, baseKey, type, time, visitnum, icon, userId, model);
        return "chat/chattable";
    }



    @GetMapping("/metable")
    public String meTable(@RequestParam(value="page",defaultValue="0") int page,
			@RequestParam(value="baseKey",required=false) String baseKey,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="time",required=false) String time,
			@RequestParam(value="visitnum",required=false) String visitnum,
			@RequestParam(value="icon",required=false) String icon,
			@SessionAttribute("userId") Long userId,Model model) {

        discussManageService.meTable(page, baseKey, type, time, visitnum, icon, userId, model);

        return "chat/chattable";

    }


    @GetMapping("/seetable")
    public String seeTable(@RequestParam(value="page",defaultValue="0") int page,
			@RequestParam(value="baseKey",required=false) String baseKey,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="time",required=false) String time,
			@RequestParam(value="visitnum",required=false) String visitnum,
			@RequestParam(value="icon",required=false) String icon,
			@SessionAttribute("userId") Long userId,Model model) {

        discussManageService.seeTable(page, baseKey, type, time, visitnum, icon, userId, model);
        return "chat/chattable";

    }


    @GetMapping("/seediscuss")
    public String seeDiscuss(@RequestParam(value = "id") Long id, @RequestParam(value = "pageNumber") Integer pageNumber, HttpSession session) {


        discussManageService.seeDiscuss(id, pageNumber, session);

        return "redirect:/replymanage";


    }


    @GetMapping("/replymanage")
    public String replyManage(Model model,HttpSession session,
			@RequestParam(value="page",defaultValue="0") int page,
			@RequestParam(value="size",defaultValue="5") int size,
			@SessionAttribute("userId") Long userId) {

        discussManageService.replyManage(model, session, page, size, userId);

        return "chat/replaymanage";

    }


    @GetMapping("/writechat")
    public String writeDiscuss(HttpServletRequest req, @SessionAttribute(value = "userId") Long userId, Model model) {


        discussManageService.writeDiscuss(req, userId, model);
        return "chat/writechat";
    }


    @RequestMapping("/adddiscuss")
    public String addDiscuss(HttpServletRequest req, @Valid Discuss menu, VoteList voteList, BindingResult br) {

        ResultVO res = BindingResultVOUtil.hasErrors(br);
        if (!ResultEnum.SUCCESS.getCode().equals(res.getCode())) {

            return null;

        } else {
            discussManageService.addDiscuss(req, menu, voteList);
            return "forward:/chatmanage";
        }


    }














}
