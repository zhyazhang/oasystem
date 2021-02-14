package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.DayManageDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.schedule.ScheduleList;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.DayManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<ScheduleList> aboutmeschedule(Long userId) {

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
