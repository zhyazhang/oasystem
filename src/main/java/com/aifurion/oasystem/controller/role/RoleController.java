package com.aifurion.oasystem.controller.role;

import com.aifurion.oasystem.entity.role.Role;
import com.aifurion.oasystem.entity.role.Rolemenu;
import com.aifurion.oasystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/23 10:52
 */

@Controller
public class RoleController {


    @Autowired
    private RoleService roleService;

    @RequestMapping("/rolemanage")
    public ModelAndView index(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size) {

        return roleService.index(page, size);
    }


    @RequestMapping("/roleser")
    public String roleser(HttpServletRequest req, Model model,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size) {

        roleService.roleser(req, model, page, size);
        return "role/roletable";
    }


    @RequestMapping("/roleset")
    public String setRole(HttpServletRequest req, Model model) {

        roleService.setRole(req, model);

        return "role/roleset";
    }


    @ResponseBody
    @RequestMapping("/powerss")
    public Boolean power(HttpServletRequest req) {

        return roleService.power(req);
    }


    @RequestMapping("addrole")
    public String addRole(HttpServletRequest req, Model model) {

        roleService.addRole(req, model);
        return "role/addrole";

    }


    @RequestMapping("modifyrole")
    public String modifyRole(HttpServletRequest req, @Valid Role role, BindingResult br) {

        roleService.modifyRole(req, role, br);
        return "redirect:/rolemanage";

    }

    @RequestMapping("deshan")
    public String deleteRole(HttpServletRequest req, Model model, HttpSession session) {

        return roleService.deleteRole(req, model, session);

    }


}
