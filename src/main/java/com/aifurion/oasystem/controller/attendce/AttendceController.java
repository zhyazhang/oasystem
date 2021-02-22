package com.aifurion.oasystem.controller.attendce;

import com.aifurion.oasystem.common.CommonMethods;
import com.aifurion.oasystem.entity.attendce.Attends;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.service.AttendceService;
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
public class AttendceController {

    @Autowired
    private AttendceService attendceService;

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


    @RequestMapping("/attendceatt")
    public String attendcePage(HttpServletRequest request, HttpSession session,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "baseKey", required = false) String baseKey,
                               @RequestParam(value = "type", required = false) String type,
                               @RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "time", required = false) String time,
                               @RequestParam(value = "icon", required = false) String icon, Model model) {


        attendceService.attendcePage(request, session, page, baseKey, type, status, time, icon, model);
        return "attendce/attendceview";
    }


    @GetMapping("/attendcelisttable")
    public String getAttendceListTable(HttpServletRequest request, Model model, HttpSession session,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "baseKey", required = false) String baseKey,
                                       @RequestParam(value = "type", required = false) String type,
                                       @RequestParam(value = "status", required = false) String status,
                                       @RequestParam(value = "time", required = false) String time,
                                       @RequestParam(value = "icon", required = false) String icon) {
        attendceService.getAttendceListTable(request, model, session, page, baseKey, type, status, time, icon);

        return "attendce/attendcelisttable";
    }


    @RequestMapping("/attendcetable")
    public String table(HttpServletRequest request, HttpSession session,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "baseKey", required = false) String baseKey,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "status", required = false) String status,
                        @RequestParam(value = "time", required = false) String time,
                        @RequestParam(value = "icon", required = false) String icon, Model model) {
        attendceService.allsortpaging(request, session, page, baseKey, type, status, time, icon, model);
        return "attendce/attendcetable";
    }


    @GetMapping("/attendcelist")
    public String getAttendceList(HttpServletRequest request, Model model, HttpSession session,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "baseKey", required = false) String baseKey,
                                  @RequestParam(value = "type", required = false) String type,
                                  @RequestParam(value = "status", required = false) String status,
                                  @RequestParam(value = "time", required = false) String time,
                                  @RequestParam(value = "icon", required = false) String icon) {


        attendceService.getAttendceList(request, model, session, page, baseKey, type, status, time, icon);

        return "attendce/attendcelist";
    }


    @GetMapping("/signin")
    public String signin(HttpSession session, Model model, HttpServletRequest request) {


        attendceService.signin(session, model);

        return "systemcontrol/signin";
    }


    @RequestMapping("/attendceweek")
    public String test3(HttpServletRequest request, HttpSession session,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "baseKey", required = false) String baseKey) {
        attendceService.weektablepaging(request, session, page, baseKey);
        return "attendce/weektable";
    }

    @RequestMapping("/realweektable")
    public String dsaf(HttpServletRequest request, HttpSession session,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "baseKey", required = false) String baseKey) {
        attendceService.weektablepaging(request, session, page, baseKey);
        return "attendce/realweektable";

    }

    @RequestMapping("/attendcemonth")
    public String test2(HttpServletRequest request, Model model, HttpSession session,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "baseKey", required = false) String baseKey) {
        attendceService.monthtablepaging(request, model, session, page, baseKey);
        return "attendce/monthtable";
    }


    @RequestMapping("/realmonthtable")
    public String dfshe(HttpServletRequest request, Model model, HttpSession session,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "baseKey", required = false) String baseKey) {
        attendceService.monthtablepaging(request, model, session, page, baseKey);
        return "attendce/realmonthtable";
    }


    @RequestMapping("/attdelete")
    public String dsfa(HttpServletRequest request, HttpSession session) {
        long aid = Long.parseLong(request.getParameter("aid"));
        attendceService.delete(aid);
        return "redirect:/attendceatt";
    }


    @RequestMapping("/attendceedit")
    public String editAttendance(@Param("aid") String aid, Model model, HttpServletRequest request, HttpSession session) {
        Long userid = Long.valueOf(session.getAttribute("userId") + "");
        if (aid == null) {
            model.addAttribute("write", 0);
        } else {
            long id = Long.parseLong(aid);
            Attends attends = attendceService.findOne(id);
            model.addAttribute("write", 1);
            model.addAttribute("attends", attends);
        }
        commonMethods.setTypeStatus(request,"aoa_attends_list","aoa_attends_list");
        return "attendce/attendceedit";
    }


    @RequestMapping("/attendceedit2")
    public String editAttendance2(HttpServletRequest request) {
        long id = Long.parseLong(request.getParameter("id"));
        Attends attends = attendceService.findOne(id);
        request.setAttribute("attends", attends);
        commonMethods.setTypeStatus(request,"aoa_attends_list","aoa_attends_list");
        return "attendce/attendceedit2";
    }


    @PostMapping("/attendcesave")
    public String test4(Model model, HttpSession session, HttpServletRequest request) {
        Long userid = Long.parseLong(session.getAttribute("userId") + "");
        String remark = request.getParameter("remark");
        String statusname = request.getParameter("status");
        SystemStatusList statusList = statusService.findByStatusModelAndStatusName("aoa_attends_list", statusname);
        long id = Long.parseLong(request.getParameter("id"));
        Attends attends = attendceService.findOne(id);
        attends.setAttendsRemark(remark);
        attends.setStatusId(statusList.getStatusId());
        attendceService.save(attends);
        //attendceService.updatereamrk(remark, id);
        return "redirect:/attendceatt";
    }


}
