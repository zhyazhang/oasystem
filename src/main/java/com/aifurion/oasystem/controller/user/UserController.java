package com.aifurion.oasystem.controller.user;

import com.aifurion.oasystem.entity.role.Role;
import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.DeptService;
import com.aifurion.oasystem.service.PositionService;
import com.aifurion.oasystem.service.RoleService;
import com.aifurion.oasystem.service.UserService;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/23 9:54
 */

@Controller
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private RoleService roleService;


    @RequestMapping("/selectdept")
    @ResponseBody
    public List<Position> selectdept(@RequestParam("selectdeptid") Long deptid) {

        return userService.selectdept(deptid);
    }


    @ResponseBody
    @RequestMapping("/useronlyname")
    public boolean useronlyname(@RequestParam("username") String username) {

        boolean b = userService.useronlyname(username);
        return b;

    }


    @RequestMapping("/deleteuser")
    public String deleteuser(@RequestParam("userid") Long userid, Model model) {

        userService.deleteUser(userid, model);
        return "/usermanage";

    }


    @PostMapping("/useredit")
    public String addUser(User user,
                          @RequestParam("deptid") Long deptid,
                          @RequestParam("positionid") Long positionid,
                          @RequestParam("roleid") Long roleid,
                          @RequestParam(value = "isbackpassword", required = false) boolean isbackpassword,
                          Model model) {


        userService.addUser(user, deptid, positionid, roleid, isbackpassword, model);

        return "/usermanage";


    }


    @GetMapping("/useredit")
    public String getUserEdit(@RequestParam(value = "userid", required = false) Long userid, Model model) {

        userService.getUserEdit(userid, model);
        return "user/edituser";
    }


    @RequestMapping("/usermanagepaging")
    public String userPaging(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             @RequestParam(value = "usersearch", required = false) String usersearch) {


        userService.userPaging(model, page, size, usersearch);
        return "user/usermanagepaging";
    }


    @RequestMapping("/userlogmanage")
    public String userLogManage() {
        return "user/userlogmanage";
    }

    @RequestMapping("/usermanage")
    public String userManage(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size) {

        userService.userManage(model, page, size);
        return "user/usermanage";
    }


}
