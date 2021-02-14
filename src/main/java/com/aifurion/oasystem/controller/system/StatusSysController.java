package com.aifurion.oasystem.controller.system;

import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 10:40
 */

@Controller
public class StatusSysController {

    @Autowired
    private StatusService statusService;


    @RequestMapping("/testsysstatus")
    public String testsysstatus(HttpServletRequest req) {
        Iterable<SystemStatusList> statusList = statusService.findAll();
        req.setAttribute("statusList", statusList);
        return "systemcontrol/statusmanage";
    }


    @RequestMapping("/statustable")
    public String statusTable(HttpServletRequest req) {
        if (!StringUtils.isEmpty(req.getParameter("name"))) {
            String name = "%" + req.getParameter("name") + "%";
            req.setAttribute("statusList", statusService.findByStatusNameLikeOrStatusModelLike(name, name));
        } else {
            Iterable<SystemStatusList> statusList = statusService.findAll();
            req.setAttribute("statusList", statusList);
        }
        return "systemcontrol/statustable";
    }

    @RequestMapping("/statusedit")
    public String typeEdit(HttpServletRequest req) {
        if (!StringUtils.isEmpty(req.getParameter("statusid"))) {
            Long statusid = Long.parseLong(req.getParameter("statusid"));
            SystemStatusList statusList = statusService.findOne(statusid);
            req.setAttribute("status", statusList);
            HttpSession session = req.getSession();
            session.setAttribute("statusid", statusid);
        }
        return "systemcontrol/statusedit";
    }


    @RequestMapping("/statuscheck")
    public String testMess(HttpServletRequest req, @Valid SystemStatusList menu, BindingResult br) {

        statusService.vaildForm(req, menu, br);
        return "systemcontrol/statusedit";

    }

    @RequestMapping("/deletestatus")
    public String deleteStatus(HttpServletRequest req) {
        Long statusId = Long.parseLong(req.getParameter("id"));
        statusService.deleteStatus(statusId);
        return "forward:/testsysstatus";
    }


}
