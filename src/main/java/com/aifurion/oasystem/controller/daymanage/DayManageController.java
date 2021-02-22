package com.aifurion.oasystem.controller.daymanage;

import com.aifurion.oasystem.entity.schedule.ScheduleList;
import com.aifurion.oasystem.service.DayManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/22 9:46
 */


@Controller
public class DayManageController {


    @Autowired
    private DayManageService dayManageService;


    @RequestMapping("/daymanage")
    private String dayManage(@SessionAttribute("userId") Long userid,
                             Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size) {

        dayManageService.dayManage(userid, model, page, size);
        return "daymanage/daymanage";
    }


    @RequestMapping("/daymanagepaging")
    private String dayManagePaging(@SessionAttribute("userId") Long userid,
                                   Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size) {

        dayManageService.dayManagePaging(userid, model, page, size);
        return "daymanage/daymanagepaging";
    }

    @RequestMapping("/aboutmeday")
    private String aboutMeDay(@SessionAttribute("userId") Long userid,
                              Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size) {

        dayManageService.aboutMeDay(userid, model, page, size);

        return "daymanage/daymanage";
    }


    @RequestMapping("/aboutmedaypaging")
    public String aboutmedaypaging(@SessionAttribute("userId") Long userid,
                                   Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size) {


        dayManageService.aboutMeDayPaging(userid, model, page, size);

        return "daymanage/daymanagepaging";
    }


    @RequestMapping("/dayedit")
    private String dayEdit(@RequestParam(value = "rcid", required = false) Long rcid,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           Model model) {


        dayManageService.dayEdit(rcid, page, size, model);
        return "daymanage/editday";
    }


    @RequestMapping("/addandchangeday")
    public String addAndChangeDay(ScheduleList scheduleList, @RequestParam("shareuser") String shareuser, BindingResult br,
                                  @SessionAttribute("userId") Long userid) {


        dayManageService.addAndChangeDay(scheduleList, shareuser, br, userid);
        return "/daymanage";
    }


    @RequestMapping("/dayremove")
    public String deleteSchedule(@RequestParam(value = "rcid") Long rcid) {

        dayManageService.deleteSchedule(rcid);

        return "/daymanage";
    }

    /**
     * 一下是日历controller
     *
     * @return
     */
    @RequestMapping("daycalendar")
    private String daycalendar() {
        return "daymanage/daycalendar";
    }

    @RequestMapping("/mycalendarload")
    @ResponseBody
    public List<ScheduleList> mycalendarload(@SessionAttribute("userId") Long userid, HttpServletResponse response) throws IOException {

        return dayManageService.aboutMeSchedule(userid);
    }


}
