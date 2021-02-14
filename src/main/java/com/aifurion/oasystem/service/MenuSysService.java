package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.system.SystemMenu;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 11:17
 */
public interface MenuSysService {

    SystemMenu save(SystemMenu menu);

    int changeSortOtherId(Integer sortId, Integer arithNum, Long parentId);

    int changeSortSelfId(Integer sortId, Integer arithNum, Long menuId);

    void findMenuSys(HttpServletRequest req, User user);

    void findAllMenuSys(HttpServletRequest req);

    int deleteThis(Long menuId);

    void getMenuTable(HttpServletRequest req);


    void changeSortId(HttpServletRequest req, Long userId);

    void menuEdit(HttpServletRequest req);


    void validForm(HttpServletRequest req, SystemMenu menu, BindingResult br);




}
