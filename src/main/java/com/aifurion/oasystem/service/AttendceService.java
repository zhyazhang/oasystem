package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.attendce.Attends;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 16:00
 */
public interface AttendceService {

    void getAttendceList(HttpServletRequest request, Model model, HttpSession session, int page, String baseKey,
                         String type, String status, String time, String icon);

    Page<Attends> singleUserPage(int page, String baseKey, long userid, Object type, Object status, Object time);


    void signin(HttpSession session, Model model);

    Integer updatetime(Date date, String hourmin, Long statusIdlong, long attid);

    void getAttendceListTable(HttpServletRequest request, Model model, HttpSession session,
                              int page, String baseKey, String type,
                              String status, String time, String icon);

    void attendcePage(HttpServletRequest request, HttpSession session, int page, String baseKey,
			String type, String status, String time, String icon,Model model) ;

    void weektablepaging(HttpServletRequest request, HttpSession session, int page, String baseKey);

    void monthtablepaging(HttpServletRequest request, Model model, HttpSession session, int page,
                          String baseKey);

    void allsortpaging(HttpServletRequest request, HttpSession session, int page, String baseKey, String type,
                       String status, String time, String icon, Model model);

    Integer delete(long aid);

    void typestatus(HttpServletRequest request);

    Attends findOne(Long id);

    void save(Attends attends);



}
