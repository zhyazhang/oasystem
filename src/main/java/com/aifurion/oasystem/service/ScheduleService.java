package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.schedule.ScheduleList;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/22 9:43
 */
public interface ScheduleService {

    List<ScheduleList> findStart(long userid);
}
