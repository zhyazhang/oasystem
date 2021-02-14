package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.mail.Inmaillist;
import com.aifurion.oasystem.entity.mail.Mailnumber;
import com.aifurion.oasystem.entity.mail.Pagemail;
import com.aifurion.oasystem.entity.note.Attachment;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 9:37
 */
public interface MailService {

    void UserpanelController();

    Page<Pagemail> recive(int page, int size, User tu, String val, String title);

    List<Map<String, Object>> mail(Page<Pagemail> mail);

    Page<Inmaillist> inmail(int page, int size, User tu, String val, String title);

    List<Map<String, Object>> maillist(Page<Inmaillist> mail);

    Page<Mailnumber> index(int page, int size, User tu, String val, Model model);

    List<Map<String, Object>> up(Page<Mailnumber> num);

    Attachment upload(MultipartFile file, User mu);

    void deleteAccount(Long id);

    boolean isContainChinese(String str);

    void pushmail(String account, String password, String reciver,
                  String name, String title, String content, String affix, String filename);

    MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,
                                  String name, String title, String content, String affix, String filename) throws UnsupportedEncodingException, MessagingException;




}
