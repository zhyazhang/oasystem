package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.mail.Inmaillist;
import com.aifurion.oasystem.entity.mail.Mailnumber;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 17:21
 */
public interface MailManageService {


    void index( Long userId, Model model, int page, int size);

    String delete(HttpServletRequest req, Long userId, Model model, int page, int size);

    void watch( Long userId, Model model, HttpServletRequest req, int page, int size);

    void star(Long userId, Model model, HttpServletRequest req, int page, int size);

    void searchByMail(Long userId, Model model, HttpServletRequest req, int page, int size);

    void account(Long userId, Model model, int page, int size);

    void sortAccount(HttpServletRequest request, Long userId, Model model, int page, int size);

    void add(Long userId, Model model, HttpServletRequest req);

    void save(HttpServletRequest request,Mailnumber mail, BindingResult br, Long userId);

    void writeMail(Model model, Long userId, HttpServletRequest request, int page, int size);

    void pushMail(MultipartFile file, HttpServletRequest request, Inmaillist mail, BindingResult br, Long userId);

    void searchByName(Model model, HttpServletRequest req, Long userId, int page, int size);

    void recentlyMail(HttpServletRequest req, Long userId, Model model, int page, int size);

    void seeMail(HttpServletRequest req,  Long userId, Model model);

    String refresh(HttpServletRequest req, Long userId, Model model, int page, int size);


}
