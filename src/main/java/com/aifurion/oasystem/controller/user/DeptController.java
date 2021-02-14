package com.aifurion.oasystem.controller.user;

import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostPersist;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 14:19
 */

@Controller
public class DeptController {


    @Autowired
    private DeptService deptService;


    @RequestMapping("/deptmanage")
    private String deptManage(Model model) {

        deptService.deptManage(model);
        return "user/deptmanage";

    }


    @PostMapping("/deptedit")
    private String addDept(@Valid Dept dept, @RequestParam("xg") String xg, BindingResult br, Model model) {

        if (deptService.addDept(dept, xg, br, model)) {
            return "user/deptedit";

        } else {
            return "/deptmanage";

        }
    }


    @GetMapping( "/deptedit")
	public String changedept(@RequestParam(value = "dept",required=false) Long deptId,Model model){
		if(deptId!=null){
			Dept dept = deptService.findOne(deptId);
			model.addAttribute("dept",dept);
		}
		return "user/deptedit";
	}



    @RequestMapping("/readdept")
	public String readdept(@RequestParam(value = "deptid") Long deptId,Model model) {

        deptService.readDept(deptId, model);
        return "user/deptread";

    }

    @RequestMapping("/deptandpositionchange")
	public String deptandpositionchange(@RequestParam("positionid") Long positionid,
			@RequestParam("changedeptid") Long changedeptid,
			@RequestParam("userid") Long userid,
			@RequestParam("deptid") Long deptid,
			Model model) {

        deptService.depTanDpositionChange(positionid, changedeptid, userid, deptid, model);
        return "/readdept";

    }


    @RequestMapping("/deletdept")
	public String deletdept(@RequestParam("deletedeptid") Long deletedeptid) {
        deptService.deletDept(deletedeptid);

        return "/deptmanage";

    }



    @RequestMapping("/deptmanagerchange")
	public String deptmanagerchange(@RequestParam(value="positionid",required=false) Long positionid,
			@RequestParam(value="changedeptid",required=false) Long changedeptid,
			@RequestParam(value="oldmanageid",required=false) Long oldmanageid,
			@RequestParam(value="newmanageid",required=false) Long newmanageid,
			@RequestParam("deptid") Long deptid,
			Model model){

        deptService.deptManageChange(positionid, changedeptid, oldmanageid, newmanageid, deptid, model);
        return "/readdept";

        }







}
