package com.aifurion.oasystem.controller.system;

import com.aifurion.oasystem.common.formVaild.BindingResultVOUtil;
import com.aifurion.oasystem.common.formVaild.ResultEnum;
import com.aifurion.oasystem.common.formVaild.ResultVO;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 20:41
 */

@Controller
public class TypeSystemController {


    @Autowired
    private TypeDao typeDao;

    @Autowired
    private TypeService typeService;



    @GetMapping("/testsystype")
    public String systemType(HttpServletRequest req) {

        Iterable<SystemTypeList> typeList = typeService.findAll();
        req.setAttribute("typeList", typeList);
        return "systemcontrol/typemanage";
    }


    @GetMapping("/typetable")
    public String typeTable(HttpServletRequest req) {
        if (!StringUtils.isEmpty(req.getParameter("name"))) {
            String name = "%" + req.getParameter("name") + "%";
            req.setAttribute("typeList", typeService.findByTypeNameLikeOrTypeModelLike(name, name));
        } else {
            Iterable<SystemTypeList> typeList = typeService.findAll();
            req.setAttribute("typeList", typeList);
        }
        return "systemcontrol/typetable";
    }

    @GetMapping("/typeedit")
	public String typeEdit(HttpServletRequest req) {
		if (!StringUtils.isEmpty(req.getParameter("typeid"))) {
			Long typeid = Long.parseLong(req.getParameter("typeid"));
			SystemTypeList typeObj = typeService.findById(typeid);
			req.setAttribute("typeObj", typeObj);
			HttpSession session = req.getSession();
			session.setAttribute("typeid", typeid);
		}
		return "systemcontrol/typeedit";
	}


	@PostMapping("/typecheck")
    public String checkForm(HttpServletRequest req, @Valid SystemTypeList menu, BindingResult br) {

        HttpSession session = req.getSession();
		Long menuId = null;
        req.setAttribute("menuObj", menu);

        ResultVO res = BindingResultVOUtil.hasErrors(br);

        typeService.checkForm(req, menu, res);

        return "systemcontrol/typeedit";


    }



	@GetMapping("/deletetype")
	public String deleteThis(HttpServletRequest req){
		Long typeId=Long.parseLong(req.getParameter("id"));
		typeService.deleteById(typeId);
		return "forward:/testsystype";
	}





















}
