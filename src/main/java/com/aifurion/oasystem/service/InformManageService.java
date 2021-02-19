package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.notice.NoticesList;
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
 * @date ：2021/2/19 15:49
 */
public interface InformManageService {


    void inform( int page, Long userId, Model model);

    void forwardOther(Long userId, Long noticeId);

    String infromDelete(HttpSession session, HttpServletRequest req);

    String informListDelete(HttpServletRequest req, HttpSession session);

    void infromList(HttpSession session, HttpServletRequest req, Model model, int page);

    void infromEdit(HttpServletRequest req, HttpSession session, Model model);

    void informShow(HttpServletRequest req, Model model);

    void verifyMessage(HttpServletRequest req, NoticesList menu, BindingResult br);

}
