package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/23 9:58
 */
public interface RoleService {

    Role findOne(Long id);

    List<Role> findAll();

    ModelAndView index(int page, int size);

    void roleser(HttpServletRequest req, Model model, int page, int size);

    void setRole(HttpServletRequest req, Model model);

    Boolean power(HttpServletRequest req);

    void addRole(HttpServletRequest req, Model model);

    void modifyRole(HttpServletRequest req,Role role, BindingResult br);

    String deleteRole(HttpServletRequest req, Model model, HttpSession session);


}
