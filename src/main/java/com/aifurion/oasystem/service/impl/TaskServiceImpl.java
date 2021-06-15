package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.task.TaskDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.dao.user.UserLogRecordDao;
import com.aifurion.oasystem.entity.user.LoginRecord;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.entity.user.UserLog;
import com.aifurion.oasystem.service.TaskService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 15:21
 */

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private TaskDao taskDao;

    @Autowired
    private UserLogRecordDao userLogRecordDao;


    @Override
    public String statisticalTask() {
        List<User> users = userDao.findAll();

        HashMap<String, Integer> hashMap = new HashMap<>();

        for (User user : users) {
            if (taskDao.countfinish(7L, user.getUserId()) > 0) {
                hashMap.put(user.getUserName(), taskDao.countfinish(7L, user.getUserId()));
            }
        }

        ArrayList<Map.Entry<String, Integer>> entriesTemp = sortMap(hashMap);
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>();

        if (entriesTemp.size() >= 5) {
            for (int i = 0; i < 5; i++) {
                entries.add(entriesTemp.get(i));
            }
        } else {
            entries = entriesTemp;
        }

        return JSONObject.toJSONString(entries);
    }


    @Override
    public String statisticalWeekLogin() {

        Integer[] week = new Integer[7];
        Calendar calendar = Calendar.getInstance();
        setToFirstDay(calendar);
        for (int i = 0; i < 7; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            week[i] = userLogRecordDao.countlog(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
        }
        return JSONObject.toJSONString(week);
    }


    @Override
    public void getUserLogRecord(int page, HttpSession session, Model model, String basekey, String time, String icon) {

        long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        setTwo(model, basekey, time, icon);
        Page<LoginRecord> page4 = userLogPaging(page, basekey, userid, time);
        model.addAttribute("page", page4);
        model.addAttribute("userloglist", page4.getContent());
        model.addAttribute("url", "morelogrecordtable");


    }

    @Override
    public void getUserLog(int page, HttpSession session, Model model, String basekey, String time, String icon) {

        long userid = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        setTwo(model, basekey, time, icon);
        Page<LoginRecord> page3 = userLogPaging(page, basekey, userid, time);
        model.addAttribute("page", page3);
        model.addAttribute("userloglist", page3.getContent());
        model.addAttribute("url", "morelogtable");


    }

    @Override
    public Page<LoginRecord> userLogPaging(int page, String basekey, Long userid, Object time) {


        Pageable pa=PageRequest.of(page, 15);
		if(!StringUtils.isEmpty(basekey)){
			//模糊
			return userLogRecordDao.findbasekey(userid, basekey, pa);
		}//0为降序 1为升序
		if(!StringUtils.isEmpty(time)){
			if("0".equals(time.toString())) {
                return userLogRecordDao.findByUserOrderBylogTimeDesc(userid, pa);
            }
			if("1".equals(time.toString())) {
                return userLogRecordDao.findByUserOrderBylogTimeAsc(userid, pa);
            }
		}else{
			return userLogRecordDao.findByUserOrderBylogTimeDesc(userid, pa);
		}
		return null;

    }



    public static ArrayList<Map.Entry<String, Integer>> sortMap(Map map) {
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        entries.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                return obj2.getValue() - obj1.getValue();
            }
        });
        return entries;
    }

    private static void setToFirstDay(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }
    }


    private void setTwo(Model model, String basekey, Object time, Object icon) {
        if (!StringUtils.isEmpty(time)) {
            model.addAttribute("time", time);
            model.addAttribute("icon", icon);
            model.addAttribute("sort", "&time=" + time + "&icon=" + icon);
        }
        if (!StringUtils.isEmpty(basekey)) {
            model.addAttribute("basekey", basekey);
            model.addAttribute("sort", "&basekey=" + basekey);
        }
    }


}
