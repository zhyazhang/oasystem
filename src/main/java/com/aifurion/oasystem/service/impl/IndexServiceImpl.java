package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.day.DayManageDao;
import com.aifurion.oasystem.dao.attendance.AttendanceDao;
import com.aifurion.oasystem.dao.discuss.DiscussDao;
import com.aifurion.oasystem.dao.file.FileListDao;
import com.aifurion.oasystem.dao.inform.InformRelationDao;
import com.aifurion.oasystem.dao.mail.MailReceiverDao;
import com.aifurion.oasystem.dao.note.DirectorDao;
import com.aifurion.oasystem.dao.notic.NoticeDao;
import com.aifurion.oasystem.dao.plan.PlanDao;
import com.aifurion.oasystem.dao.process.NotepaperDao;
import com.aifurion.oasystem.dao.process.ProcessListDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.task.TaskUserDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.dao.user.UserLogDao;
import com.aifurion.oasystem.entity.attendance.Attendance;
import com.aifurion.oasystem.entity.mail.MailReceiver;
import com.aifurion.oasystem.entity.notice.NoticeUserRelation;
import com.aifurion.oasystem.entity.notice.NoticesList;
import com.aifurion.oasystem.entity.plan.Plan;
import com.aifurion.oasystem.entity.process.Notepaper;
import com.aifurion.oasystem.entity.process.ProcessList;
import com.aifurion.oasystem.entity.schedule.ScheduleList;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.task.Taskuser;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.entity.user.UserLog;
import com.aifurion.oasystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 11:07
 */


@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private MenuSysService menuSysService;
    @Autowired
    private DayManageService dayManageService;
    @Autowired
    private DayManageDao dayManageDao;
    @Autowired
    private InformService informService;
    @Autowired
    private InformRelationService informRelationService;
    @Autowired
    private InformRelationDao informRelationDao;
    @Autowired
    private MailReceiverDao mailReceiverDao;
    @Autowired
    private TaskUserDao taskUserDao;
    @Autowired
    private UserLogDao userLogDao;
    @Autowired
    private FileListDao fileListDao;
    @Autowired
    private DirectorDao directorDao;
    @Autowired
    private DiscussDao discussDao;
    @Autowired
    private StatusDao statusDao;

    @Autowired
    private TypeDao typeDao;

    @Autowired
    private NoticeDao noticeDao;


    @Autowired
    private AttendanceDao attendanceDao;


    @Autowired
    private PlanDao planDao;


    @Autowired
    private NotepaperDao notepaperDao;


    @Autowired
    private ProcessListDao processListDao;


    @Override
    public boolean initIndex(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        if (StringUtils.isEmpty(session.getAttribute("userId"))) {
            return true;
        }

        long userId = Long.parseLong(String.valueOf(session.getAttribute("userId")));

        User user = userDao.findById(userId).get();

        menuSysService.findMenuSys(request, user);

        List<ScheduleList> aboutmeschedule = dayManageService.aboutMeSchedule(userId);

        for (ScheduleList schedule : aboutmeschedule) {


            if (schedule.getIsreminded() != null && !schedule.getIsreminded()) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

                String start = simpleDateFormat.format(schedule.getStartTime());

                String now = simpleDateFormat.format(new Date());

                try {

                    long startTime = simpleDateFormat.parse(start).getTime();
                    long nowTime = simpleDateFormat.parse(now).getTime();

                    long time = startTime - nowTime;

                    if (0 < time && time < 86400000) {
                        NoticesList noticesList = new NoticesList();

                        noticesList.setTypeId(11L);
                        noticesList.setStatusId(15L);
                        noticesList.setTitle("你有一个日程即将开始");

                        noticesList.setUrl("/daycalendar");
                        noticesList.setUserId(userId);
                        noticesList.setNoticeTime(new Date());

                        NoticesList list = informService.save(noticesList);
                        informRelationService.save(new NoticeUserRelation(list, user, false));
                        schedule.setIsreminded(true);
                        dayManageDao.save(schedule);

                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        }
        //通知
        List<NoticeUserRelation> notice = informRelationDao.findByReadAndUserId(false, user);
        //邮件
        List<MailReceiver> mail = mailReceiverDao.findByReadAndDelAndReciverId(false, false, user);
        //新任务
        List<Taskuser> task = taskUserDao.findByUserIdAndStatusId(user, 3);
        model.addAttribute("notice", notice.size());
        model.addAttribute("mail", mail.size());
        model.addAttribute("task", task.size());
        model.addAttribute("user", user);
        //展示用户操作记录 由于现在没有登陆 不能获取用户id
        List<UserLog> userLogs = userLogDao.findByUser(userId);
        request.setAttribute("userLogList", userLogs);

        return false;
    }

    @Override
    public void initContent(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();

        long userId = Long.parseLong(String.valueOf(session.getAttribute("userId")));

        User user = userDao.findById(userId).get();

        request.setAttribute("user", user);

        //计算三个模块的记录条数
        request.setAttribute("filenum", fileListDao.count());

        request.setAttribute("directornum", directorDao.count());

        request.setAttribute("discussnum", discussDao.count());

        List<Map<String, Object>> noticeList = noticeDao.findMyNoticeLimit(userId);


        List<Map<String, Object>> tempList = new ArrayList<>();


        for (Map<String, Object> map : noticeList) {

            Map<String, Object> tempMap = new HashMap<>(map);
            tempMap.put("status", statusDao.findById(Long.parseLong(map.get("status_id").toString())).get().getStatusName());
            tempMap.put("type", typeDao.findById(Long.parseLong(map.get("type_id").toString())).get().getTypeName());
            tempMap.put("statusColor", statusDao.findById(Long.parseLong(map.get("status_id").toString())).get().getStatusColor());
            tempMap.put("userName", userDao.findById(Long.parseLong(map.get("user_id").toString())).get().getUserName());
            tempMap.put("deptName", userDao.findById(Long.parseLong(map.get("user_id").toString())).get().getDept().getDeptName());
            tempList.add(tempMap);

        }

        showalist(model, userId);
        model.addAttribute("noticeList", tempList);

        //列举计划

        List<Plan> plans = planDao.findByUserlimit(userId);
        model.addAttribute("planList", plans);

        List<SystemTypeList> typeLists = typeDao.findByTypeModel("aoa_plan_list");
        List<SystemStatusList> statusLists = statusDao.findByStatusModel("aoa_plan_list");

        model.addAttribute("ptypelist", typeLists);
        model.addAttribute("pstatuslist", statusLists);

        //列举便签

        List<Notepaper> notepapers = notepaperDao.findByUserIdOrderByCreateTimeDesc(userId);

        model.addAttribute("notepaperList", notepapers);

        //列举几个流程记录

        List<ProcessList> processLists = processListDao.findlastthree(userId);

        model.addAttribute("processlist", processLists);

        List<SystemStatusList> systemStatusListList = statusDao.findByStatusModel("aoa_process_list");

        model.addAttribute("prostatuslist", systemStatusListList);


    }


    private void showalist(Model model, Long userId) {
        // 显示用户当天最新的记录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String nowdate = sdf.format(date);
        Attendance aList = attendanceDao.findlastest(nowdate, userId);
        if (aList != null) {
            String type = typeDao.findname(aList.getTypeId());
            model.addAttribute("type", type);
        }
        model.addAttribute("alist", aList);
    }


}

