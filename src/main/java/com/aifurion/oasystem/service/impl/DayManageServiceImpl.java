package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.day.DayManageDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.schedule.ScheduleList;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.DayManageService;
import com.aifurion.oasystem.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 11:45
 */

@Service
public class DayManageServiceImpl implements DayManageService {


    @Autowired
    private UserDao userDao;

    @Autowired
    private DayManageDao dayManageDao;

    @Autowired
    private TypeDao typeDao;


    @Autowired
    private StatusDao statusDao;

    @Autowired
    private ProcessService processService;


    @Override
    public void deleteSchedule(Long rcid) {


        ScheduleList rc = dayManageDao.findById(rcid).get();

        dayManageDao.delete(rc);


    }

    @Override
    public void addAndChangeDay(ScheduleList scheduleList, String shareUser, BindingResult br, Long userid) {

        User user = userDao.findById(userid).get();
        List<User> users = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(shareUser, ";");

        while (st.hasMoreElements()) {
            users.add(userDao.findByUserName(st.nextToken()));
        }

        scheduleList.setUser(user);
        if (users.size() > 0) {
            scheduleList.setUsers(users);
        }

        dayManageDao.save(scheduleList);


    }

    @Override
    public void dayEdit(Long rcid, int page, int size, Model model) {

        processService.user(page, size, model);
        List<SystemTypeList> types = typeDao.findByTypeModel("aoa_schedule_list");
        List<SystemStatusList> statuses = statusDao.findByStatusModel("aoa_schedule_list");
        ScheduleList rc = null;
        if (rcid != null) {
            rc = dayManageDao.findById(rcid).get();
        }

        model.addAttribute("types", types);
        model.addAttribute("statuses", statuses);
        model.addAttribute("rc", rc);


    }

    @Override
    public void aboutMeDayPaging(Long userid, Model model, int page, int size) {

        List<SystemTypeList> types = typeDao.findByTypeModel("aoa_schedule_list");
        List<SystemStatusList> statuses = statusDao.findByStatusModel("aoa_schedule_list");

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "user"));
        Pageable pa = PageRequest.of(page, size, sort);
        User user = userDao.findById(userid).get();
        List<User> users = new ArrayList<>();
        users.add(user);
        Page<ScheduleList> aboutmeday = dayManageDao.findByUsersIn(users, pa);

        List<ScheduleList> scheduleLists = aboutmeday.getContent();

        model.addAttribute("schedules", scheduleLists);
        model.addAttribute("types", types);
        model.addAttribute("statuses", statuses);
        model.addAttribute("page", aboutmeday);

        model.addAttribute("url", "aboutmedaypaging");


    }

    @Override
    public void aboutMeDay(Long userid, Model model, int page, int size) {


        List<SystemTypeList> types = typeDao.findByTypeModel("aoa_schedule_list");
        List<SystemStatusList> statuses = statusDao.findByStatusModel("aoa_schedule_list");

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "user"));
        Pageable pa = PageRequest.of(page, size, sort);
        User user = userDao.findById(userid).get();
        List<User> users = new ArrayList<>();
        users.add(user);
        Page<ScheduleList> aboutmeday = dayManageDao.findByUsersIn(users, pa);

        List<ScheduleList> scheduleLists = aboutmeday.getContent();

        model.addAttribute("schedules", scheduleLists);
        model.addAttribute("types", types);
        model.addAttribute("statuses", statuses);
        model.addAttribute("page", aboutmeday);
        model.addAttribute("url", "aboutmedaypaging");


    }

    @Override
    public void dayManagePaging(Long userid, Model model, int page, int size) {


        List<SystemTypeList> types = typeDao.findByTypeModel("aoa_schedule_list");
        List<SystemStatusList> statuses = statusDao.findByStatusModel("aoa_schedule_list");

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "user"));
        Pageable pa = PageRequest.of(page, size, sort);
        User user = userDao.findById(userid).get();
        Page<ScheduleList> myday = dayManageDao.findByUser(user, pa);

        List<ScheduleList> scheduleLists = myday.getContent();
        model.addAttribute("types", types);
        model.addAttribute("statuses", statuses);
        model.addAttribute("schedules", scheduleLists);
        model.addAttribute("page", myday);
        model.addAttribute("url", "daymanagepaging");
        model.addAttribute("ismyday", 1);


    }

    @Override
    public void dayManage(Long userid, Model model, int page, int size) {

        List<SystemTypeList> types = typeDao.findByTypeModel("aoa_schedule_list");
        List<SystemStatusList> statuses = statusDao.findByStatusModel("aoa_schedule_list");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "statusId"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "createTime"));
        Sort sort = Sort.by(orders);
        Pageable pa = PageRequest.of(page, size, sort);
        User user = userDao.findById(userid).get();
        Page<ScheduleList> myday = dayManageDao.findByUser(user, pa);

        List<ScheduleList> scheduleLists = myday.getContent();

        model.addAttribute("schedules", scheduleLists);
        model.addAttribute("types", types);
        model.addAttribute("statuses", statuses);
        model.addAttribute("page", myday);
        model.addAttribute("url", "daymanagepaging");
        model.addAttribute("ismyday", 1);


    }

    @Override
    public List<ScheduleList> aboutMeSchedule(Long userId) {

        User user = userDao.findById(userId).get();
        List<User> users = new ArrayList<>();
        users.add(user);
        List<ScheduleList> aboutmerc = new ArrayList<>();

        List<ScheduleList> myschedule = dayManageDao.findByUser(user);
        List<ScheduleList> otherschedule = dayManageDao.findByUsersIn(users);

        for (ScheduleList scheduleList : myschedule) {
            aboutmerc.add(scheduleList);
        }

        for (ScheduleList scheduleList : otherschedule) {
            aboutmerc.add(scheduleList);
        }


        for (ScheduleList scheduleList : aboutmerc) {
            User user1 = scheduleList.getUser();
            scheduleList.setUsername(user1.getRealName());

        }

        return aboutmerc;
    }
}
