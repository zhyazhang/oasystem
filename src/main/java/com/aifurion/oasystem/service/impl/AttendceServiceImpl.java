package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.common.CommonMethods;
import com.aifurion.oasystem.config.StringtoDate;
import com.aifurion.oasystem.dao.attendce.AttendceDao;
import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.attendce.Attends;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.AttendceService;
import com.aifurion.oasystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 16:20
 */

@Service
public class AttendceServiceImpl implements AttendceService {

    @Autowired
    private AttendceDao attendceDao;

    @Autowired
    private TypeDao typeDao;

    @Autowired
    private StatusDao statusDao;

    @Autowired
    private UserDao userDao;


    @Autowired
    private UserService userService;

    private Date start, end;

    private String month;

    DefaultConversionService service = new DefaultConversionService();

    @Autowired
    private CommonMethods commonMethods;


    @Override
    public void attendcePage(HttpServletRequest request, HttpSession session,
                             int page, String baseKey, String type,
                             String status, String time, String icon, Model model) {


        allsortpaging(request, session, page, baseKey, type, status, time, icon, model);
    }

    @Override
    public void getAttendceListTable(HttpServletRequest request, Model model,
                                     HttpSession session, int page, String baseKey,
                                     String type, String status, String time, String icon) {

        signsortpaging(request, model, session, page, baseKey, type, status, time, icon);

    }


    @Override
    public void getAttendceList(HttpServletRequest request,
                                Model model,
                                HttpSession session,
                                int page,
                                String baseKey,
                                String type,
                                String status,
                                String time,
                                String icon) {
        Long userid = Long.valueOf(String.valueOf(session.getAttribute("userId")));
        CommonMethods.setSomething(baseKey, type, status, time, icon, model);
        Page<Attends> page2 = singleUserPage(page, baseKey, userid, type, status, time);


        commonMethods.setTypeStatus(request,"aoa_attends_list","aoa_attends_list");


        request.setAttribute("alist", page2.getContent());
        for (Attends attends : page2.getContent()) {
            System.out.println(attends);
        }
        request.setAttribute("page", page2);
        request.setAttribute("url", "attendcelisttable");


    }

    @Override
    public Page<Attends> singleUserPage(int page, String baseKey, long userid, Object type, Object status, Object time) {

        Pageable pa = PageRequest.of(page, 10);
        //0为降序 1为升序
        if (!StringUtils.isEmpty(baseKey)) {
            // 查询
            System.out.println(baseKey);
            attendceDao.findonemohu(baseKey, userid, pa);
        }
        if (!StringUtils.isEmpty(type)) {
            if (type.toString().equals("0")) {
                //降序
                return attendceDao.findByUserOrderByTypeIdDesc(userid, pa);
            } else {
                //升序
                return attendceDao.findByUserOrderByTypeIdAsc(userid, pa);
            }
        }
        if (!StringUtils.isEmpty(status)) {
            if (status.toString().equals("0")) {
                return attendceDao.findByUserOrderByStatusIdDesc(userid, pa);
            } else {
                return attendceDao.findByUserOrderByStatusIdAsc(userid, pa);
            }
        }
        if (!StringUtils.isEmpty(time)) {
            if (time.toString().equals("0")) {
                return attendceDao.findByUserOrderByAttendsTimeDesc(userid, pa);
            } else {
                return attendceDao.findByUserOrderByAttendsTimeAsc(userid, pa);
            }
        } else {
            // 第几页 以及页里面数据的条数
            return attendceDao.findByUserOrderByAttendsTimeDesc(userid, pa);
        }
    }

    @Override
    public void signin(HttpSession session, Model model) {


        InetAddress ia = null;

        //首先获取ip
        try {
            ia = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String attendip = ia.getHostAddress();
        // 时间规范
        String start = "08:00:00", end = "17:00:00";
        service.addConverter(new StringtoDate());
        // 状态默认是正常
        long typeId, statusId = 10;
        Attends attends = null;
        Long userId = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        User user = userDao.findById(userId).get();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String nowdate = sdf.format(date);
        // 星期 判断该日期是星期几
        SimpleDateFormat sdf3 = new SimpleDateFormat("EEEE");
        // 截取时分
        SimpleDateFormat sdf4 = new SimpleDateFormat("HH:mm");
        // 截取时分秒
        SimpleDateFormat sdf5 = new SimpleDateFormat("HH:mm:ss");

        // 一周当中的星期几
        String weekofday = sdf3.format(date);
        // 时分
        String hourmin = sdf4.format(date);

        // 时分秒
        String hourminsec = sdf5.format(date);
        //System.out.println("星期" + weekofday + "时分" + hourmin + "时分秒" + hourminsec);
        //System.out.println(date);
        Long aid = null;

        // 查找用户当天的所有记录
        Integer count = attendceDao.countrecord(nowdate, userId);
        if (hourminsec.compareTo(end) > 0) {
            // 在17之后签到无效
            System.out.println("----不能签到");
            model.addAttribute("error", "1");
        }
        if (hourminsec.compareTo("05:00:00") < 0) {
            //在凌晨5点之前不能签到
            System.out.println("----不能签到");
            model.addAttribute("error", "2");
        } else if ((hourminsec.compareTo("05:00:00") > 0) && (hourminsec.compareTo(end) < 0)) {
            // 明确一点就是一个用户一天只能产生两条记录
            if (count == 0) {
                if (hourminsec.compareTo(end) < 0) {
                    // 没有找到当天的记录就表示此次点击是上班 就是用来判断该记录的类型
                    // 上班id8
                    typeId = 8;
                    // 上班就只有迟到和正常
                    if (hourminsec.compareTo(start) > 0) {
                        // 迟于规定时间 迟到
                        statusId = 11;
                    } else if (hourminsec.compareTo(start) < 0) {
                        statusId = 10;
                    }
                    attends = new Attends(typeId, statusId, date, hourmin, weekofday, attendip, user);
                    attendceDao.save(attends);
                }
            }
            if (count == 1) {
                // 找到当天的一条记录就表示此次点击是下班
                // 下班id9
                typeId = 9;
                // 下班就只有早退和正常
                if (hourminsec.compareTo(end) > 0) {
                    // 在规定时间晚下班正常
                    statusId = 10;
                } else if (hourminsec.compareTo(end) < 0) {
                    // 在规定时间早下班早退
                    statusId = 12;
                }
                attends = new Attends(typeId, statusId, date, hourmin, weekofday, attendip, user);
                attendceDao.save(attends);
            }
            if (count >= 2) {
                // 已经是下班的状态了 大于2就是修改考勤时间了
                // 下班id9
                if (hourminsec.compareTo(end) > 0) { // 最进一次签到在规定时间晚下班正常
                    statusId = 10;
                } else if (hourminsec.compareTo(end) < 0) {
                    // 最进一次签到在规定时间早下班早退
                    statusId = 12;
                }
                aid = attendceDao.findoffworkid(nowdate, userId);
                Attends attends2 = attendceDao.findById(aid).get();
                attends2.setAttendsIp(attendip);
                attendceDao.save(attends2);
                updatetime(date, hourmin, statusId, aid);
                Attends aList = attendceDao.findlastest(nowdate, userId);
            }
        }
        // 显示用户当天最新的记录
        Attends aList = attendceDao.findlastest(nowdate, userId);
        if (aList != null) {
            String type = typeDao.findname(aList.getTypeId());
            model.addAttribute("type", type);
        }
        model.addAttribute("alist", aList);


    }

    @Override
    public Integer updatetime(Date date, String hourmin, Long statusIdlong, long attid) {


        return attendceDao.updateatttime(date, hourmin, statusIdlong, attid);
    }





    //单个用户的排序和分页
    private void signsortpaging(HttpServletRequest request, Model model, HttpSession session, int page, String baseKey,
                                String type, String status, String time, String icon) {
        Long userid = Long.valueOf(String.valueOf(session.getAttribute("userId")));
        commonMethods.setSomething(baseKey, type, status, time, icon, model);
        Page<Attends> page2 = singlepage(page, baseKey, userid, type, status, time);
        commonMethods.setTypeStatus(request,"aoa_attends_list","aoa_attends_list");
        request.setAttribute("alist", page2.getContent());
        request.setAttribute("page", page2);
        request.setAttribute("url", "attendcelisttable");
    }

    public Page<Attends> singlepage(int page, String baseKey, long userid, Object type, Object status, Object time) {
        Pageable pa = PageRequest.of(page, 10);
        //0为降序 1为升序
        if (!StringUtils.isEmpty(baseKey)) {
            // 查询
            System.out.println(baseKey);
            attendceDao.findonemohu(baseKey, userid, pa);
        }
        if (!StringUtils.isEmpty(type)) {
            if (type.toString().equals("0")) {
                //降序
                return attendceDao.findByUserOrderByTypeIdDesc(userid, pa);
            } else {
                //升序
                return attendceDao.findByUserOrderByTypeIdAsc(userid, pa);
            }
        }
        if (!StringUtils.isEmpty(status)) {
            if (status.toString().equals("0")) {
                return attendceDao.findByUserOrderByStatusIdDesc(userid, pa);
            } else {
                return attendceDao.findByUserOrderByStatusIdAsc(userid, pa);
            }
        }
        if (!StringUtils.isEmpty(time)) {
            if (time.toString().equals("0")) {
                return attendceDao.findByUserOrderByAttendsTimeDesc(userid, pa);
            } else {
                return attendceDao.findByUserOrderByAttendsTimeAsc(userid, pa);
            }
        } else {
            // 第几页 以及页里面数据的条数
            return attendceDao.findByUserOrderByAttendsTimeDesc(userid, pa);
        }

    }


    @Override
    public void allsortpaging(HttpServletRequest request, HttpSession session, int page, String baseKey, String type,
                              String status, String time, String icon, Model model) {
        CommonMethods.setSomething(baseKey, type, status, time, icon, model);
        Long userId = Long.parseLong(String.valueOf(session.getAttribute("userId")));
        List<Long> ids = new ArrayList<>();
        List<User> users = userDao.findByFatherId(userId);
        for (User user : users) {
            ids.add(user.getUserId());
        }
        if (ids.size() == 0) {
            ids.add(0L);
        }
        User user = userDao.findById(userId).get();
        commonMethods.setTypeStatus(request,"aoa_attends_list","aoa_attends_list");
        Page<Attends> page2 = paging(page, baseKey, ids, type, status, time);
        request.setAttribute("alist", page2.getContent());
        request.setAttribute("page", page2);
        request.setAttribute("url", "attendcetable");
    }


    public Page<Attends> paging(int page, String baseKey, List<Long> user, Object type, Object status, Object time) {
        Pageable pa = PageRequest.of(page, 10);
        if (!StringUtils.isEmpty(baseKey)) {
            // 模糊查询
            return attendceDao.findsomemohu(baseKey, user, pa);
        }
        if (!StringUtils.isEmpty(type)) {
            if ("0".equals(type.toString())) {
                //降序
                return attendceDao.findByUserOrderByTypeIdDesc(user, pa);
            } else {
                System.out.println("22");
                //升序
                return attendceDao.findByUserOrderByTypeIdAsc(user, pa);
            }
        }
        if (!StringUtils.isEmpty(status)) {
            if ("0".equals(status.toString())) {
                return attendceDao.findByUserOrderByStatusIdDesc(user, pa);
            } else {
                return attendceDao.findByUserOrderByStatusIdAsc(user, pa);
            }
        }
        if (!StringUtils.isEmpty(time)) {
            if ("0".equals(time.toString())) {
                return attendceDao.findByUserOrderByAttendsTimeDesc(user, pa);
            } else {
                return attendceDao.findByUserOrderByAttendsTimeAsc(user, pa);
            }
        } else {
            return attendceDao.findByUserOrderByAttendsTimeDesc(user, pa);
        }


    }

    @Override
    public void weektablepaging(HttpServletRequest request, HttpSession session, int page, String baseKey) {


        String starttime = request.getParameter("starttime");
		String endtime = request.getParameter("endtime");
		// 格式转化
		service.addConverter(new StringtoDate());
		Date startdate = service.convert(starttime, Date.class);
		Date enddate = service.convert(endtime, Date.class);

		//用来查找该用户下面管理的所有用户信息
		Long userId = Long.parseLong(String.valueOf(session.getAttribute("userId")));
		List<Long> ids = new ArrayList<>();
		Page<User> userspage =userService.findMyEmployUser(page, baseKey, userId);
		for (User user : userspage) {
			ids.add(user.getUserId());
		}
		if (ids.size() == 0) {
			ids.add(0L);
		}

		//找到某个管理员下面的所有用户的信息 保证传过来的是正确的数据 分页之后可以使用全局变量来记住开始和结束日期
        if (startdate != null && enddate != null) {

            start = startdate;
            end = enddate;
        }
        if (startdate == null && enddate == null) {

            startdate = start;
        }
        enddate=end;
		List<Attends> alist = attendceDao.findoneweek(startdate, enddate, ids);
		Set<Attends> attenceset = new HashSet<>();
		for (User user : userspage) {
			for (Attends attence : alist) {
				if (Objects.equals(attence.getUser().getUserId(), user.getUserId())) {
					attenceset.add(attence);
				}
			}
			user.setaSet(attenceset);
		}
		String[] weekday = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
		request.setAttribute("ulist", userspage.getContent());
		request.setAttribute("page", userspage);
		request.setAttribute("weekday", weekday);
        request.setAttribute("url", "realweektable");


    }


    @Override
    public void monthtablepaging(HttpServletRequest request, Model model, HttpSession session, int page, String baseKey) {


        Integer offnum,toworknum;
		Long userId = Long.parseLong(session.getAttribute("userId") + "");
		List<Long> ids = new ArrayList<>();
		Page<User> userspage =userService.findMyEmployUser(page, baseKey, userId);
		for (User user : userspage) {
			ids.add(user.getUserId());
		}
		if (ids.size() == 0) {
			ids.add(0L);
		}
		String month = request.getParameter("month");

		if(month!=null) {
            this.month=month;
        } else {
            month=this.month;
        }

		Map<String, List<Integer>> uMap = new HashMap<>();
		List<Integer> result = null;

		for (User user : userspage) {
			result = new ArrayList<>();
			//当月该用户下班次数
			offnum=attendceDao.countoffwork(month, user.getUserId());
			//当月该用户上班次数
			toworknum=attendceDao.counttowork(month, user.getUserId());
			for (long statusId = 10; statusId < 13; statusId++) {
				//这里面记录了正常迟到早退等状态
				if(statusId==12) {
                    result.add(attendceDao.countnum(month, statusId, user.getUserId())+toworknum-offnum);
                } else {
                    result.add(attendceDao.countnum(month, statusId, user.getUserId()));
                }
			}
			//添加请假和出差的记录//应该是查找 使用sql的sum（）函数来统计出差和请假的次数

			if(attendceDao.countothernum(month, 46L, user.getUserId())!=null) {
                result.add(attendceDao.countothernum(month, 46L, user.getUserId()));
            } else {
                result.add(0);
            }
			if(attendceDao.countothernum(month, 47L, user.getUserId())!=null) {
                result.add(attendceDao.countothernum(month, 47L, user.getUserId()));
            } else {
                result.add(0);
            }
			//这里记录了旷工的次数 还有请假天数没有记录 旷工次数=30-8-请假次数-某天签到次数
			//这里还有请假天数没有写
			Date date=new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
			String date_month=sdf.format(date);
			if(month!=null){
				if(month.compareTo(date_month)>=0) {
                    result.add(0);
                } else {
                    result.add(30-8-offnum);
                }
			}

			uMap.put(user.getUserName(), result);
		}
		model.addAttribute("uMap", uMap);
		model.addAttribute("ulist", userspage.getContent());
		model.addAttribute("page", userspage);
		model.addAttribute("url", "realmonthtable");

    }

    @Override
    public Integer delete(long aid) {


        return attendceDao.delete(aid);
    }


    @Override
    public Attends findOne(Long id) {

        return attendceDao.findById(id).get();
    }

    @Override
    public void save(Attends attends) {

        attendceDao.save(attends);

    }
}
