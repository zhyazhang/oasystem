package com.aifurion.oasystem.controller.system;

import com.aifurion.oasystem.entity.system.SystemMenu;
import com.aifurion.oasystem.service.MenuSysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 9:21
 */

@Controller
public class MenuSystemController {


    @Autowired
    private MenuSysService menuSysService;





    @RequestMapping("testsysmenu")
	public String testsysmenu(HttpServletRequest req) {
		menuSysService.findAllMenuSys(req);
		return "systemcontrol/menumanage";
	}


	@GetMapping("/menutable")
    public String getMenuTable(HttpServletRequest req) {

        menuSysService.getMenuTable(req);
        return "systemcontrol/menutable";

    }



    @GetMapping("/changeSortId")
    public String changeSortId(HttpServletRequest req,@SessionAttribute("userId")Long userId) {

        menuSysService.changeSortId(req, userId);
        return "redirect:/testsysmenu";

    }


    @RequestMapping("/menuedit")
    public String menuEdit(HttpServletRequest req) {

        menuSysService.menuEdit(req);
        return "systemcontrol/menuedit";


    }

    @PostMapping("/test111")
    public String validForm(HttpServletRequest req, @Valid SystemMenu menu, BindingResult br) {
        menuSysService.validForm(req, menu, br);
        return "forward:/menuedit";
    }


    @PostMapping("/deletethis")
    public String delete(HttpServletRequest req) {

        Long menuId=Long.parseLong(req.getParameter("id"));
		menuSysService.deleteThis(menuId);
		return "forward:/testsysmenu";

    }



















}
