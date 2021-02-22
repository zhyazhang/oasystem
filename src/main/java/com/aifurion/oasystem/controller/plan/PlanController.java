package com.aifurion.oasystem.controller.plan;

import com.aifurion.oasystem.common.CommonMethods;
import com.aifurion.oasystem.entity.plan.Plan;
import com.aifurion.oasystem.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/22 10:41
 */


@Controller
public class PlanController {


    @Autowired
    private PlanService planService;

    @Autowired
    private CommonMethods commonMethods;


    @RequestMapping("/plandelete")
    public String deletePlan(HttpServletRequest request, HttpSession session) {

        long realUserId = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        long pid = Long.parseLong(request.getParameter("pid"));

        return planService.deletePlan(realUserId, pid);
    }

    // 计划管理
    @GetMapping("/planview")
    public String planManage(Model model, HttpServletRequest request,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "baseKey", required = false) String baseKey,
                             @RequestParam(value = "type", required = false) String type,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "time", required = false) String time,
                             @RequestParam(value = "icon", required = false) String icon) {


        Long userid = Long.parseLong(String.valueOf(request.getSession().getAttribute("userId")));

        planService.sortPaging(model, request, userid, page, baseKey, type, status, time, icon);
        return "plan/planview";
    }


    @GetMapping(value = "/planviewtable")
    public String planViewTable(Model model, HttpServletRequest request,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "baseKey", required = false) String baseKey,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "status", required = false) String status,
                                @RequestParam(value = "time", required = false) String time,
                                @RequestParam(value = "icon", required = false) String icon) {
        Long userid = Long.parseLong(String.valueOf(request.getSession().getAttribute("userId")));

        planService.sortPaging(model, request, userid, page, baseKey, type, status, time, icon);
        return "plan/planviewtable";
    }


    // 计划报表
    @RequestMapping("/myplan")
    public String myPlan(HttpServletRequest request, Model model, HttpSession session,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "baseKey", required = false) String baseKey) {
        planService.planTablePaging(request, model, session, page, baseKey);

        return "plan/plantable";
    }


    // 真正的报表
    @RequestMapping("/realplantable")
    public String realPlanTable(HttpServletRequest request, Model model, HttpSession session,
                         @RequestParam(value = "pid", required = false) String pid,
                         @RequestParam(value = "comment", required = false) String comment,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "baseKey", required = false) String baseKey) {
        planService.planTablePaging(request, model, session, page, baseKey);
        if (!StringUtils.isEmpty(pid)) {
            Plan plan = planService.findOne(Long.valueOf(pid));
            if (plan.getPlanComment() == null) {

                plan.setPlanComment(comment);
            } else {
                plan.setPlanComment(plan.getPlanComment() + comment);
            }
            planService.save(plan);
        }
        return "plan/realplantable";
    }



    // 我的编辑
	@RequestMapping("/planedit")
	public String editPlan(HttpServletRequest request, Model model) {
		long pid = Long.parseLong(request.getParameter("pid"));
		if (!StringUtils.isEmpty(request.getAttribute("errormess"))) {
			request.setAttribute("errormess", request.getAttribute("errormess"));
			request.setAttribute("plan", request.getAttribute("plan2"));
		} else if (!StringUtils.isEmpty(request.getAttribute("success"))) {
			request.setAttribute("success", request.getAttribute("success"));
			request.setAttribute("plan", request.getAttribute("plan2"));
		}
		// 新建
		if (pid == -1) {
			model.addAttribute("plan", null);
			model.addAttribute("pid", pid);
		} else if (pid > 0) {
			Plan plan = planService.findOne(pid);
			model.addAttribute("plan", plan);
			model.addAttribute("pid", pid);
		}

		commonMethods.setTypeStatus(request, "aoa_plan_list", "aoa_plan_list");
		return "plan/planedit";
	}



	@RequestMapping(value = "plansave", method = RequestMethod.POST)
	public String savePlan(@RequestParam("file") MultipartFile file, HttpServletRequest req, @Valid Plan plan2,
                           BindingResult br) throws IllegalStateException, IOException {

        planService.savePlan(file, req, plan2, br);

		return "forward:/planedit";
	}



}

