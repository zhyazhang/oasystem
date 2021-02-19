package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.process.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:06
 */
public interface ProcedureService {

    void bursement(Model model,  Long userId, HttpServletRequest request,
                     int page, int size);


    String apply(MultipartFile filePath, HttpServletRequest req, Bursement bu, BindingResult br, Long userId);

    void flowManage(Long userId, Model model, int page, int size);

    void ser(Long userId, Model model, HttpServletRequest req, int page, int size);

    void auding(Long userId, Model model, int page, int size);


    void serch(Long userId, Model model, HttpServletRequest req, int page, int size);


    String particular( Long userId, Model model, HttpServletRequest req);


    void auditing( Long userId, Model model, HttpServletRequest req, int page, int size);

    String save( Long userId, Model model, HttpServletRequest req, Reviewed reviewed);


    void evemoney(Model model, Long userId, HttpServletRequest req, int page, int size);


    String moneyeve(MultipartFile filePath, HttpServletRequest req, EvectionMoney eve, BindingResult br,
                  Long userId, Model model);


    void evection(Model model, Long userId, HttpServletRequest request, int page, int size);

    String evec(MultipartFile filePath, HttpServletRequest req, Evection eve, BindingResult br, Long userId);


    void overtime(Model model, Long userId, HttpServletRequest request, int page, int size);


    String over(HttpServletRequest req, Overtime eve, BindingResult br, Long userId);

    void holiday(Model model,  Long userId, HttpServletRequest request, int page, int size);


    String holi(MultipartFile filePath, HttpServletRequest req, Holiday eve, BindingResult br, Long userId, Model model);


    String regu(HttpServletRequest req, Regular eve, BindingResult br, Long userId, Model model);


    String res(HttpServletRequest req, Resign eve, BindingResult br, Long userId, Model model);

    String dele(HttpServletRequest req, Long userId, Model model);

    void downFile(HttpServletResponse response, Long fileid);

    void image(Model model, HttpServletResponse response, @SessionAttribute("userId") Long userId, HttpServletRequest request);

}
