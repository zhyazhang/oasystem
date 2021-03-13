package com.aifurion.oasystem.controller.attendance;

import com.aifurion.oasystem.common.CommonMethods;
import com.aifurion.oasystem.entity.attendance.Attendance;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.service.AttendanceService;
import com.aifurion.oasystem.service.StatusService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 15:59
 */

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private CommonMethods commonMethods;


    /**
     * 获取签到列表
     *
     * @param request
     * @param model
     * @param session
     * @param page
     * @param baseKey
     * @param type
     * @param status
     * @param time
     * @param icon
     * @return
     */


    @RequestMapping("/attendanceatt")
    public String attendancePage(HttpServletRequest request, HttpSession session,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "baseKey", required = false) String baseKey,
                                 @RequestParam(value = "type", required = false) String type,
                                 @RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "time", required = false) String time,
                                 @RequestParam(value = "icon", required = false) String icon, Model model) {


        attendanceService.attendancePage(request, session, page, baseKey, type, status, time, icon, model);
        return "attendance/attendanceview";
    }


    @GetMapping("/attendancetablelist")
    public String getAttendanceListTable(HttpServletRequest request, Model model, HttpSession session,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "baseKey", required = false) String baseKey,
                                       @RequestParam(value = "type", required = false) String type,
                                       @RequestParam(value = "status", required = false) String status,
                                       @RequestParam(value = "time", required = false) String time,
                                       @RequestParam(value = "icon", required = false) String icon) {
        attendanceService.getAttendanceListTable(request, model, session, page, baseKey, type, status, time, icon);

        return "attendance/attendancelisttable";
    }


    @RequestMapping("/attendancetable")
    public String attendanceManagement(HttpServletRequest request, HttpSession session,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "baseKey", required = false) String baseKey,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "status", required = false) String status,
                        @RequestParam(value = "time", required = false) String time,
                        @RequestParam(value = "icon", required = false) String icon, Model model) {
        attendanceService.allSortPaging(request, session, page, baseKey, type, status, time, icon, model);
        return "attendance/attendancetable";
    }


    @GetMapping("/attendancelist")
    public String getAttendanceList(HttpServletRequest request, Model model, HttpSession session,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "baseKey", required = false) String baseKey,
                                  @RequestParam(value = "type", required = false) String type,
                                  @RequestParam(value = "status", required = false) String status,
                                  @RequestParam(value = "time", required = false) String time,
                                  @RequestParam(value = "icon", required = false) String icon) {


        attendanceService.getAttendanceList(request, model, session, page, baseKey, type, status, time, icon);

        return "attendance/attendancelist";
    }


    @GetMapping("/signin")
    public String signin(HttpSession session, Model model, HttpServletRequest request) {


        attendanceService.signIn(session, model);

        return "systemcontrol/signin";
    }



    @RequestMapping("/attendanceweek")
    public String test3(HttpServletRequest request, HttpSession session,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "baseKey", required = false) String baseKey) {



        attendanceService.weekTablePaging(request, session, page, baseKey,null,null);
        return "attendance/weektable";
    }


    @RequestMapping("/realweektable")
    public String dsaf(HttpServletRequest request, HttpSession session,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "baseKey", required = false) String baseKey,
                       @RequestParam(value = "starttime") String starttime,
                       @RequestParam(value = "endtime") String endtime) {


        attendanceService.weekTablePaging(request, session, page, baseKey,starttime,endtime);
        return "attendance/realweektable";

    }

    @RequestMapping("/attendancemonth")
    public String test2(HttpServletRequest request, Model model, HttpSession session,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "baseKey", required = false) String baseKey) {
        attendanceService.monthTablePaging(request, model, session, page, baseKey);
        return "attendance/monthtable";
    }


    @RequestMapping("/realmonthtable")
    public String dfshe(HttpServletRequest request, Model model, HttpSession session,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "baseKey", required = false) String baseKey) {
        attendanceService.monthTablePaging(request, model, session, page, baseKey);
        return "attendance/realmonthtable";
    }


    @RequestMapping("/attendancedelete")
    public String dsfa(HttpServletRequest request, HttpSession session) {
        long aid = Long.parseLong(request.getParameter("aid"));
        attendanceService.deleteAttendance(aid);
        return "redirect:/attendanceatt";
    }


    @RequestMapping("/attendanceedit")
    public String editAttendance(@Param("aid") String aid, Model model, HttpServletRequest request, HttpSession session) {
        Long userid = Long.valueOf(session.getAttribute("userId") + "");
        if (aid == null) {
            model.addAttribute("write", 0);
        } else {
            long id = Long.parseLong(aid);
            Attendance attends = attendanceService.findOneAttendance(id);
            model.addAttribute("write", 1);
            model.addAttribute("attends", attends);
        }
        commonMethods.setTypeStatus(request,"aoa_attends_list","aoa_attends_list");
        return "attendance/attendanceedit";
    }


    @RequestMapping("/attendanceedit2")
    public String editAttendance2(HttpServletRequest request) {
        long id = Long.parseLong(request.getParameter("id"));
        Attendance attends = attendanceService.findOneAttendance(id);
        request.setAttribute("attends", attends);
        commonMethods.setTypeStatus(request,"aoa_attends_list","aoa_attends_list");
        return "attendance/attendanceedit2";
    }


    @PostMapping("/attendancesave")
    public String test4(Model model, HttpSession session, HttpServletRequest request) {
        Long userid = Long.parseLong(session.getAttribute("userId") + "");
        String remark = request.getParameter("remark");
        String statusname = request.getParameter("status");
        SystemStatusList statusList = statusService.findByStatusModelAndStatusName("aoa_attends_list", statusname);
        long id = Long.parseLong(request.getParameter("id"));
        Attendance attends = attendanceService.findOneAttendance(id);
        attends.setAttendsRemark(remark);
        attends.setStatusId(statusList.getStatusId());
        attendanceService.save(attends);
        //attendceService.updatereamrk(remark, id);
        return "redirect:/attendanceatt";
    }


}
