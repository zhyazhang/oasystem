package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.plan.Plan;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/22 10:42
 */
public interface PlanService {


    String deletePlan(Long realUserId, Long pid);

    void sortPaging(Model model, HttpServletRequest request, Long userid,
                    int page, String baseKey, String type, String status, String time, String icon);

    void planTablePaging(HttpServletRequest request, Model model, HttpSession session, int page,
                         String baseKey);

    void savePlan( MultipartFile file, HttpServletRequest req,Plan plan2,
                    BindingResult br);


    Plan findOne(Long id);

    void save(Plan plan);



}
