package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.attendance.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 16:00
 */
public interface AttendanceService {

    void getAttendanceList(HttpServletRequest request, Model model, HttpSession session, int page, String baseKey,
                         String type, String status, String time, String icon);

    Page<Attendance> singleUserPage(int page, String baseKey, long userid, Object type, Object status, Object time);


    void signIn(HttpSession session, Model model);

    Integer updateTime(Date date, String hourmin, Long statusIdlong, long attid);

    void getAttendanceListTable(HttpServletRequest request, Model model, HttpSession session,
                              int page, String baseKey, String type,
                              String status, String time, String icon);

    void attendancePage(HttpServletRequest request, HttpSession session, int page, String baseKey,
			String type, String status, String time, String icon,Model model) ;

    void weekTablePaging(HttpServletRequest request, HttpSession session, int page, String baseKey, String starttime, String endtime);

    void monthTablePaging(HttpServletRequest request, Model model, HttpSession session, int page,
                          String baseKey);

    void allSortPaging(HttpServletRequest request, HttpSession session, int page, String baseKey, String type,
                       String status, String time, String icon, Model model);

    void deleteAttendance(long aid);


    Attendance findOneAttendance(Long id);

    void save(Attendance attends);



}
