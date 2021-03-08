package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.process.Notepaper;
import com.aifurion.oasystem.entity.user.User;
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
 * @date ：2021/3/8 18:38
 */
public interface UserPanelService {


    void userPanel(Long userId, Model model, HttpServletRequest req, int page, int size);

    void getPanel(Long userId, Model model, int page, int size);

    void savePanel(Notepaper notepaper, Long userId, String content);


    boolean deletePaper(Long paperId, Long userId);

    void saveUser(MultipartFile filePath, HttpServletRequest request,  User user, BindingResult br, Long userId);


}
