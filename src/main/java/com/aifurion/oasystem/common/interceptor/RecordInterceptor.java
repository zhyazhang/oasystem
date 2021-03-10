package com.aifurion.oasystem.common.interceptor;

import com.aifurion.oasystem.common.tool.Tool;
import com.aifurion.oasystem.dao.role.RolepowerListDao;
import com.aifurion.oasystem.dao.system.SystemMenuDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.dao.user.UserLogDao;
import com.aifurion.oasystem.entity.role.Rolemenu;
import com.aifurion.oasystem.entity.system.SystemMenu;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.entity.user.UserLog;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/12 9:11
 */


@Component
public class RecordInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        if (!StringUtils.isEmpty(session.getAttribute("userId"))) {

            UserDao userDao = Tool.getBean(UserDao.class, request);
            RolepowerListDao rolepowerListDao = Tool.getBean(RolepowerListDao.class, request);

           // long userId = Long.parseLong(session.getAttribute("userId"));
            long userId = Long.parseLong(String.valueOf(session.getAttribute("userId")));
            User user = userDao.findById(userId).get();

            List<Rolemenu> parentMenus = rolepowerListDao
                    .findByParentDisplayAll(0L, user.getRole().getRoleId(), true, true);

            List<Rolemenu> childMenus = rolepowerListDao
                    .findByParentsDisplay(0L, user.getRole().getRoleId(), true, true);

            List<Rolemenu> allMenus = new ArrayList<>();

            String url = request.getRequestURL().toString();

            String limit = "unlimited";

            if (parentMenus.size() > 0) {
                allMenus.addAll(parentMenus);
            }

            if (childMenus.size() > 0) {
                allMenus.addAll(childMenus);
            }

            for (Rolemenu menu : allMenus) {
                if (!menu.getMenuUrl().equals(url)) {
                    return true;
                } else {
                    request.getRequestDispatcher(limit).forward(request, response);

                }
            }

        } else {
            response.sendRedirect("/logins");
            return false;
        }

        return false;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        HttpSession session = request.getSession();

        UserDao userDao = Tool.getBean(UserDao.class, request);

        SystemMenuDao systemMenuDao = Tool.getBean(SystemMenuDao.class, request);

        UserLogDao userLogDao = Tool.getBean(UserLogDao.class, request);

        UserLog userLog = new UserLog();

        String ip = InetAddress.getLocalHost().getHostAddress();

        userLog.setIpAddr(ip);
        userLog.setUrl(request.getServletPath());
        userLog.setLogTime(new Date());

        Long id = (Long) session.getAttribute("id");

        if (StringUtils.isEmpty(id)) {
            return;
        }
        userLog.setUser(userDao.findById(id).get());

        List<SystemMenu> systemMenus = (List<SystemMenu>) systemMenuDao.findAll();

        for (SystemMenu menu : systemMenus) {

            if (menu.getMenuUrl().equals(request.getServletPath())) {

                if (!userLogDao.findByUserLast(1L).getUrl().equals(menu.getMenuUrl())) {

                    userLog.setTitle(menu.getMenuName());
                    userLogDao.save(userLog);
                }

            }

        }

    }
}
