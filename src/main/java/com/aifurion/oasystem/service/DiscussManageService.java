package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.discuss.Discuss;
import com.aifurion.oasystem.entity.discuss.VoteList;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:16
 */
public interface DiscussManageService {


    void getChatList(int page, Model model, HttpSession session);

    void adminManage(int page, HttpSession session, Long userId, Model model);

    void chatManage(int page, Long userId,Model model,HttpSession session);

    boolean deleteDiscuss(String name,  Long userId, Long discussId);

    void chatTable(int page, String baseKey, String type, String time, String visitnum, String icon, Long userId,Model model);

    void meTable(int page, String baseKey, String type, String time, String visitnum, String icon, Long userId,Model model);

    void seeTable(int page, String baseKey, String type, String time, String visitnum, String icon, Long userId, Model model);


    void seeDiscuss(Long id, Integer pageNumber, HttpSession session);


    void replyManage(Model model,HttpSession session, int page, int size, Long userId);

    void writeDiscuss(HttpServletRequest req, Long userId, Model mode);

    void addDiscuss(HttpServletRequest req, Discuss menu, VoteList voteList);


}
