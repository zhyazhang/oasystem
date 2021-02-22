package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.schedule.ScheduleList;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 11:45
 */
public interface DayManageService {

    List<ScheduleList> aboutMeSchedule(Long userId);

    void dayManage(Long userid, Model model, int page, int size);


    void dayManagePaging(Long userid, Model model, int page, int size);

    void aboutMeDay(Long userid, Model model,  int page, int size);

    void aboutMeDayPaging(Long userid, Model model, int page, int size);

    void dayEdit(Long rcid, int page, int size, Model model);

    void addAndChangeDay(ScheduleList scheduleList, String shareUser, BindingResult br, Long userid);

    void deleteSchedule(Long rcid);




}
