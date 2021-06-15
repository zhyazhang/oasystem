package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.user.LoginRecord;
import com.aifurion.oasystem.entity.user.UserLog;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 15:15
 */
public interface TaskService {

    String statisticalTask();

    String statisticalWeekLogin();

    void getUserLogRecord(int page, HttpSession session, Model model, String basekey, String time, String icon);

    Page<LoginRecord> userLogPaging(int page, String basekey, Long userid, Object time);

    void getUserLog(int page, HttpSession session, Model model, String basekey, String time, String icon);

}
