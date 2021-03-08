package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.IndexDao;
import com.aifurion.oasystem.dao.role.RoleDao;
import com.aifurion.oasystem.dao.role.RolepowerListDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.role.Role;
import com.aifurion.oasystem.entity.role.Rolemenu;
import com.aifurion.oasystem.entity.role.Rolepowerlist;
import com.aifurion.oasystem.entity.system.SystemMenu;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.RoleService;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/23 9:59
 */

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private IndexDao indexDao;

    @Autowired
    private RolepowerListDao rolepowerListDao;


    @Override
    public String deleteRole(HttpServletRequest req, Model model, HttpSession session) {

		Long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")).trim());
		User user=userDao.findById(userid).get();
		String id=null;
		if(!StringUtil.isEmpty(req.getParameter("id"))){
			id=req.getParameter("id");

		}
		Long lid=Long.parseLong(id);
		if(user.getSuperman().equals(true)){
			List<User> useist=userDao.findRole(lid);
			if(useist.size()>0){
				model.addAttribute("error", "此角色下还有职员，不允许删除。");
				return "common/proce";
			}else{
				Role r=roleDao.findById(lid).get();
				roleDao.delete(r);
			}
		}else{
			model.addAttribute("error", "只有超级管理员才能操作。");
			return "common/proce";
		}

        return null;


    }

    @Override
    public void modifyRole(HttpServletRequest req, Role role, BindingResult br) {

        String id = null;
        if (!StringUtil.isEmpty(req.getParameter("id"))) {
            id = req.getParameter("id");
        }
        if (!StringUtil.isEmpty(id)) {
            Long lid = Long.parseLong(id);
            Role roles = roleDao.findById(lid).get();
            roles.setRoleName(role.getRoleName());
            roleDao.save(roles);

        } else {
            Role rolep = roleDao.save(role);
            List<SystemMenu> menulist = indexDao.findAll();
            index(menulist, rolep);
        }


    }

    @Override
    public void addRole(HttpServletRequest req, Model model) {


        String id = null;
        Role role = new Role();

        if (!StringUtil.isEmpty(req.getParameter("id"))) {
            id = req.getParameter("id");
            Long lid = Long.parseLong(id);
            role = roleDao.findById(lid).get();

        }
        model.addAttribute("role", role);


    }

    @Override
    public Boolean power(HttpServletRequest req) {

        Long roleid = Long.parseLong(req.getParameter("roleid"));
        String content = req.getParameter("content").trim();
        Long menuid = Long.parseLong(req.getParameter("menuid"));
        Rolepowerlist rolepower = rolepowerListDao.findByRoleIdAndMenuId(roleid, menuid);
        rolepower.setCheck("选中".equals(content));
        rolepowerListDao.save(rolepower);
        return true;
    }

    @Override
    public void setRole(HttpServletRequest req, Model model) {

        Long roleid = Long.parseLong(req.getParameter("id"));

        Role role = roleDao.findById(roleid).get();
        List<Rolemenu> oneMenuAll = rolepowerListDao.findByParentAll(0L, roleid);
        List<Rolemenu> twoMenuAll = rolepowerListDao.findByParents(0L, roleid);

        model.addAttribute("oneMenuAll", oneMenuAll);
        model.addAttribute("twoMenuAll", twoMenuAll);
        model.addAttribute("roleid", roleid);
        model.addAttribute("rolename", role.getRoleName());


    }

    @Override
    public void roleser(HttpServletRequest req, Model model, int page, int size) {

        Pageable pa = PageRequest.of(page, size);
        Page<Role> pagerole = null;
        List<Role> rolelist = null;
        String val = null;


        if (!StringUtil.isEmpty(req.getParameter("val"))) {
            val = req.getParameter("val").trim();
        }

        if (!StringUtil.isEmpty(val)) {
            pagerole = roleDao.findbyrolename(val, pa);
            model.addAttribute("sort", "&val=" + val);
        } else {
            pagerole = roleDao.findAll(pa);
        }
        rolelist = pagerole.getContent();
        model.addAttribute("page", pagerole);
        model.addAttribute("rolelist", rolelist);
        model.addAttribute("url", "roleser");


    }

    @Override
    public ModelAndView index(int page, int size) {

        Pageable pa = PageRequest.of(page, size);
        ModelAndView mav = new ModelAndView("role/rolemanage");
        Page<Role> pagerole = roleDao.findAll(pa);
        List<Role> rolelist = pagerole.getContent();
        mav.addObject("page", pagerole);
        mav.addObject("rolelist", rolelist);
        mav.addObject("url", "roleser");
        return mav;
    }

    @Override
    public Role findOne(Long id) {
        return roleDao.findById(id).get();
    }

    @Override
    public List<Role> findAll() {
        return roleDao.findAll();
    }


    public void index(List<SystemMenu> menulist, Role rolep) {

        for (SystemMenu systemMenu : menulist) {

            rolepowerListDao.save(new Rolepowerlist(rolep, systemMenu));
        }
    }
}
