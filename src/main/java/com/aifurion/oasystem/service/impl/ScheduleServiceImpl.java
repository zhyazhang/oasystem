package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.schedule.ScheduleDao;
import com.aifurion.oasystem.entity.schedule.ScheduleList;
import com.aifurion.oasystem.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/22 9:44
 */

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleDao scheduleDao;


    @Override
    public List<ScheduleList> findStart(long userid) {
        List<ScheduleList> lists = scheduleDao.findStart(userid);


        return lists;
    }
}
