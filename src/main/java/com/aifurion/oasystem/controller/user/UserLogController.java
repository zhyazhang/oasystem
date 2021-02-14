package com.aifurion.oasystem.controller.user;

import com.aifurion.oasystem.entity.schedule.ScheduleList;
import com.aifurion.oasystem.service.ScheduleService;
import com.aifurion.oasystem.service.TaskService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 15:11
 */

@Controller
public class UserLogController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ScheduleService scheduleService;


    @GetMapping("/countweeklogin")
    public void countWeekLogin(HttpServletResponse response) throws IOException {

        String json = taskService.statisticalWeekLogin();

        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/json;charset=UTF-8");
        response.getWriter().write(json);
    }


    @GetMapping("/counttasknum")
    public void countTask(HttpServletResponse response) throws IOException {

        String task = taskService.statisticalTask();
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/json;charset=UTF-8");
        response.getWriter().write(task);
    }


    @GetMapping("/morelogrecord")
    public String getUserRecord(@RequestParam(value = "page", defaultValue = "0") int page,
                                HttpSession session, Model model,
                                @RequestParam(value = "baseKey", required = false) String basekey,
                                @RequestParam(value = "time", required = false) String time,
                                @RequestParam(value = "icon", required = false) String icon) {

        taskService.getUserLogRecord(page, session, model, basekey, time, icon);
        return "user/userlogrecordmanage";
    }

    @GetMapping("/morelogrecordtable")
    public String findUserRecord(@RequestParam(value = "page", defaultValue = "0") int page,
                                 HttpSession session, Model model,
                                 @RequestParam(value = "baseKey", required = false) String basekey,
                                 @RequestParam(value = "time", required = false) String time,
                                 @RequestParam(value = "icon", required = false) String icon) {

        taskService.getUserLogRecord(page, session, model, basekey, time, icon);
        return "user/userlogrecordmanagetable";

    }

    @RequestMapping("/littlecalendar")
    public String test3df(HttpSession session, HttpServletResponse response) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        List<Object> list = new ArrayList<>();
        List<ScheduleList> dates = scheduleService.findStart(userid);
        for (ScheduleList scheduleList : dates) {
            list.add(sdf.format(scheduleList.getStartTime()));
        }
        String json = JSONObject.toJSONString(list);
        System.out.println(json);
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("text/json;charset=UTF-8");
        response.getWriter().write(json);
        return null;
    }


    @RequestMapping("/morelog")
    public String test3df(@RequestParam(value = "page", defaultValue = "0") int page,
                          HttpSession session, Model model,
                          @RequestParam(value = "baseKey", required = false) String basekey,
                          @RequestParam(value = "time", required = false) String time,
                          @RequestParam(value = "icon", required = false) String icon) {
        taskService.getUserLog(page, session, model, basekey, time, icon);
        return "user/userlogmanage";
    }


    @RequestMapping("/morelogtable")
    public String test3dfrt(@RequestParam(value = "page", defaultValue = "0") int page,
                            HttpSession session, Model model,
                            @RequestParam(value = "baseKey", required = false) String basekey,
                            @RequestParam(value = "time", required = false) String time,
                            @RequestParam(value = "icon", required = false) String icon) {
        taskService.getUserLog(page, session, model, basekey, time, icon);
        return "user/userlogmanagetable";

    }


}
