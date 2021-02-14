package com.aifurion.oasystem.service;

import com.aifurion.oasystem.common.enums.VoteEnum;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 15:45
 */
public interface ReplyService {


    void reply(HttpServletRequest req, int page, int size, Long userId, Model model);

    void likeThis(HttpServletRequest req, Long userId);

    void likeThisFun(HttpServletRequest req, Long userId);

    void replyPaging(HttpServletRequest req, Long selecttype, Long selectsort, int page, int size, Long userId, Model model);

    boolean replyDelete(HttpServletRequest req, int page, int size, Long userId, Model model);

    VoteEnum voteHandle(HttpServletRequest req, Long userId, Model model);

    void likeUserLoad(HttpServletRequest req, Model model, Long userId,String module);




}
