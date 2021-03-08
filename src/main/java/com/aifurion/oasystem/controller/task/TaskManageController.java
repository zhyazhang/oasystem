package com.aifurion.oasystem.controller.task;

import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.task.Tasklist;
import com.aifurion.oasystem.entity.task.Tasklogger;
import com.aifurion.oasystem.entity.task.Taskuser;
import com.aifurion.oasystem.entity.user.Dept;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.*;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/21 11:28
 */

@Controller
public class TaskManageController {

    @Autowired
    private TaskManageService taskManageService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private TypeService typeService;


    @Autowired
    private StatusService statusService;

    @Autowired
    private PositionService positionService;


    /**
     * 任务管理表格
     *
     * @return
     */
    @RequestMapping("/taskmanage")
    public String taskManageForm(Model model, @SessionAttribute("userId") Long userId, @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "10") int size) {

        // 通过发布人id找用户
        User user = userService.findOne(userId);
        // 根据发布人id查询任务
        Page<Tasklist> tasklist = taskManageService.getTaskListByRelease(page, size, null, user);
        List<Map<String, Object>> list = taskManageService.getReleaseInfo(tasklist, user);

        model.addAttribute("tasklist", list);
        model.addAttribute("page", tasklist);
        model.addAttribute("url", "paixu");
        return "task/taskmanage";
    }


    /**
     * 各种排序
     */
    @RequestMapping("/paixu")
    public String sortTask(HttpServletRequest request,
                        @SessionAttribute("userId") Long userId, Model model,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "10") int size) {

        // 通过发布人id找用户
        User user = userService.findOne(userId);
        String val = null;
        if (!StringUtil.isEmpty(request.getParameter("val"))) {
            val = request.getParameter("val").trim();
            model.addAttribute("sort", "&val=" + val);
        }

        Page<Tasklist> tasklist = taskManageService.getTaskListByRelease(page, size, val, user);
        List<Map<String, Object>> list = taskManageService.getReleaseInfo(tasklist, user);
        model.addAttribute("tasklist", list);
        model.addAttribute("page", tasklist);
        model.addAttribute("url", "paixu");

        return "task/managetable";

    }


    /**
     * 点击新增任务
     */
    @RequestMapping("/addtask")
    public ModelAndView addNewTask(@SessionAttribute("userId") Long userId,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        ModelAndView mav = new ModelAndView("task/addtask");
        // 查询类型表
        Iterable<SystemTypeList> typelist = typeService.findAll();
        // 查询状态表
        Iterable<SystemStatusList> statuslist = statusService.findAll();
        // 查询部门下面的员工
        Page<User> userPage = userService.findByFatherId(userId, pageable);
        List<User> userList = userPage.getContent();
        // 查询部门表
        Iterable<Dept> deptlist = deptService.findAll();
        // 查职位表
        Iterable<Position> poslist = positionService.findAll();
        mav.addObject("typelist", typelist);
        mav.addObject("statuslist", statuslist);
        mav.addObject("emplist", userList);
        mav.addObject("deptlist", deptlist);
        mav.addObject("poslist", poslist);
        mav.addObject("page", userPage);
        mav.addObject("url", "names");
        mav.addObject("qufen", "任务");
        return mav;
    }


    /**
     * 新增任务保存
     */
    @RequestMapping("/addtasks")
    public String addSaveTask(@SessionAttribute("userId") Long userId, HttpServletRequest request) {
        User userList = userService.findOne(userId);
        Tasklist list = (Tasklist) request.getAttribute("tasklist");
        request.getAttribute("success");
        list.setUsersId(userList);
        list.setPublishTime(new Date());
        list.setModifyTime(new Date());
        taskManageService.saveTasks(list);
        // 分割任务接收人
        StringTokenizer st = new StringTokenizer(list.getReciverlist(), ";");
        while (st.hasMoreElements()) {
            User reciver = userService.findUserByName(st.nextToken());
            Taskuser taskuser = new Taskuser();
            taskuser.setTaskId(list);
            taskuser.setUserId(reciver);
            taskuser.setStatusId(list.getStatusId());
            // 存任务中间表
            taskManageService.saveTaskUser(taskuser);

        }

        return "redirect:/taskmanage";
    }


    /**
     * 修改任务
     */
    @RequestMapping("/edittasks")
    public ModelAndView editTasks(HttpServletRequest req, @SessionAttribute("userId") Long userId,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pa = PageRequest.of(page, size);
        ModelAndView mav = new ModelAndView("task/edittask");
        // 得到链接中的任务id
        String taskid = req.getParameter("id");
        Long ltaskid = Long.parseLong(taskid);
        // 通过任务id得到相应的任务
        Tasklist task = taskManageService.findOne(ltaskid);
        // 得到状态id
        Long statusid = task.getStatusId().longValue();
        // 得到类型id
        Long typeid = task.getTypeId();
        // 查看状态表
        SystemStatusList status = statusService.findOne(statusid);
        // 查询类型表
        SystemTypeList type = typeService.findOne(typeid);

        // 查询部门下面的员工
        Page<User> pagelist = userService.findByFatherId(userId, pa);
        List<User> emplist = pagelist.getContent();

        // 查询部门表
        Iterable<Dept> deptlist = deptService.findAll();
        // 查职位表
        Iterable<Position> poslist = positionService.findAll();
        mav.addObject("type", type);
        mav.addObject("status", status);
        mav.addObject("emplist", emplist);
        mav.addObject("deptlist", deptlist);
        mav.addObject("poslist", poslist);
        mav.addObject("task", task);
        mav.addObject("page", pagelist);
        mav.addObject("url", "names");
        mav.addObject("qufen", "任务");
        return mav;
    }


    /**
     * 修改任务确定
     */
    @RequestMapping("/update")
    public String update(Tasklist task, @SessionAttribute("userId") Long userId) {
        User userlist = userService.findOne(userId);
        task.setUsersId(userlist);
        task.setPublishTime(new Date());
        task.setModifyTime(new Date());
        taskManageService.saveTasks(task);

        // 分割任务接收人 还要查找联系人的主键
        StringTokenizer st = new StringTokenizer(task.getReciverlist(), ";");
        while (st.hasMoreElements()) {
            User reciver = userService.findUserByName(st.nextToken());
            Long pkid = userService.findPkId(task.getTaskId(), reciver.getUserId());
            Taskuser taskuser = new Taskuser();
            taskuser.setPkId(pkid);
            taskuser.setTaskId(task);
            taskuser.setUserId(reciver);
            taskuser.setStatusId(task.getStatusId());
            // 存任务中间表
            taskManageService.saveTaskUser(taskuser);

        }

        return "redirect:/taskmanage";

    }


    /**
     * 查看任务
     */
    @RequestMapping("/seetasks")
    public ModelAndView seeTasks(HttpServletRequest req) {
        ModelAndView mav = new ModelAndView("task/seetask");
        // 得到任务的 id
        Long ltaskid = Long.parseLong(req.getParameter("id"));
        // 通过任务id得到相应的任务
        Tasklist task = taskManageService.findOne(ltaskid);
        Long statusid = task.getStatusId().longValue();

        // 根据状态id查看状态表
        SystemStatusList status = statusService.findOne(statusid);
        // 查看状态表
        Iterable<SystemStatusList> statuslist = statusService.findAll();
        // 查看发布人
        User user = userService.findOne(task.getUsersId().getUserId());
        // 查看任务日志表
        List<Tasklogger> logger = taskManageService.findTaskLoggerByTaskId(ltaskid);
        mav.addObject("task", task);
        mav.addObject("user", user);
        mav.addObject("status", status);
        mav.addObject("loggerlist", logger);
        mav.addObject("statuslist", statuslist);
        return mav;
    }


    /**
     * 存反馈日志
     *
     * @return
     */
    @RequestMapping("/tasklogger")
    public String taskLogger(Tasklogger logger, @SessionAttribute("userId") Long userId) {
        User userlist = userService.findOne(userId);
        logger.setCreateTime(new Date());
        logger.setUsername(userlist.getUserName());
        // 存日志
        taskManageService.saveTaskLogger(logger);
        // 修改任务状态
        taskManageService.updateStatusId(logger.getTaskId().getTaskId(), logger.getLoggerStatusid());
        // 修改任务中间表状态
        taskManageService.updateUserStatusId(logger.getTaskId().getTaskId(), logger.getLoggerStatusid());

        return "redirect:/taskmanage";

    }


    /**
     * 我的任务
     */
    @RequestMapping("/mytask")
    public String myTask(@SessionAttribute("userId") Long userId, Model model,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pa = PageRequest.of(page, size);
        Page<Tasklist> tasklist = taskManageService.getTaskListByReceive(userId, null, page, size);

        Page<Tasklist> tasklist2 = taskManageService.findByTickingIsNotNull(pa);
        if (tasklist != null) {
            List<Map<String, Object>> list = taskManageService.getReceiveInfo(tasklist, userId);
            model.addAttribute("page", tasklist);
            model.addAttribute("tasklist", list);
        } else {
            List<Map<String, Object>> list2 = taskManageService.getReceiveInfo(tasklist2, userId);
            model.addAttribute("page", tasklist2);
            model.addAttribute("tasklist", list2);
        }
        model.addAttribute("url", "mychaxun");
        return "task/mytask";

    }


    /**
     * 在我的任务里面进行查询
     *
     * @throws ParseException
     */
    @RequestMapping("/mychaxun")
    public String selectFromMyTasks(HttpServletRequest request, @SessionAttribute("userId") Long userId, Model model,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size) throws ParseException {

        String title = null;
        if (!StringUtil.isEmpty(request.getParameter("title"))) {
            title = request.getParameter("title").trim();
        }
        Page<Tasklist> tasklist = taskManageService.getTaskListByReceive(userId, title, page, size);
        List<Map<String, Object>> list = taskManageService.getReceiveInfo(tasklist, userId);
        model.addAttribute("tasklist", list);
        model.addAttribute("page", tasklist);
        model.addAttribute("url", "mychaxun");
        model.addAttribute("sort", "&title=" + title);
        return "task/mytasklist";
    }


    @RequestMapping("myseetasks")
    public ModelAndView seeMyTasks(HttpServletRequest req, @SessionAttribute("userId") Long userId) {

        ModelAndView mav = new ModelAndView("task/myseetask");
        // 得到任务的 id
        String taskid = req.getParameter("id");

        Long ltaskid = Long.parseLong(taskid);
        // 通过任务id得到相应的任务
        Tasklist task = taskManageService.findOne(ltaskid);

        // 查看状态表
        Iterable<SystemStatusList> statuslist = statusService.findAll();
        // 查询接收人的任务状态
        Long ustatus = taskManageService.findStatusByUserIdAndTaskId(userId, ltaskid);

        SystemStatusList status = statusService.findOne(ustatus);

        // 查看发布人
        User user = userService.findOne(task.getUsersId().getUserId());
        // 查看任务日志表
        List<Tasklogger> logger = taskManageService.findTaskLoggerByTaskId(ltaskid);

        mav.addObject("task", task);
        mav.addObject("user", user);
        mav.addObject("status", status);
        mav.addObject("statuslist", statuslist);
        mav.addObject("loggerlist", logger);
        return mav;

    }


    /**
	 * 从我的任务查看里面修改状态和日志
	 */
	@RequestMapping("uplogger")
	public String updateLogger(Tasklogger logger, @SessionAttribute("userId") Long userId) {
		// 查找用户
		User user = userService.findOne(userId);
		// 查任务
		Tasklist task = taskManageService.findOne(logger.getTaskId().getTaskId());
		logger.setCreateTime(new Date());
		logger.setUsername(user.getUserName());
		// 存日志
		taskManageService.saveTaskLogger(logger);

		// 修改任务中间表状态
		Long pkid = userService.findPkId(logger.getTaskId().getTaskId(), userId);
		Taskuser taskuser = new Taskuser();
		taskuser.setPkId(pkid);
		taskuser.setTaskId(task);
		taskuser.setUserId(user);
		if (!Objects.isNull(logger.getLoggerStatusid())) {

			taskuser.setStatusId(logger.getLoggerStatusid());
		}
		// 存任务中间表
		taskManageService.saveTaskUser(taskuser);

		// 修改任务状态
		// 通过任务id查看总状态

		List<Integer> statu = taskManageService.findTaskStatusByTaskId(logger.getTaskId().getTaskId());
		// 选出最小的状态id 修改任务表里面的状态
		Integer min = statu.get(0);
		for (Integer integer : statu) {
			if (integer < min) {
				min = integer;
			}
		}

		int up = taskManageService.updateStatusId(logger.getTaskId().getTaskId(), min);

		return "redirect:/mytask";

	}

	/**
	 * 根据发布人这边删除任务和相关联系
	 * @param req
	 * @return
	 */
	@RequestMapping("/shanchu")
	public String deleteTaskAndOther(HttpServletRequest req, @SessionAttribute("userId") Long userId) {
		// 得到任务的 id
		Long loggerTaskId = Long.parseLong(req.getParameter("id"));

		// 根据任务id找出这条任务
		Tasklist task = taskManageService.findOne(loggerTaskId);
		if(task.getUsersId().getUserId().equals(userId)){
			// 删除日志表
			int i=taskManageService.deleteLogger(loggerTaskId);
			// 分割任务接收人 还要查找联系人的主键并删除接收人中间表
			StringTokenizer st = new StringTokenizer(task.getReciverlist(), ";");
			while (st.hasMoreElements()) {
				User reciver = userService.findUserByName(st.nextToken());
				Long pkid = userService.findPkId(task.getTaskId(), reciver.getUserId());
				int m=taskManageService.delete(pkid);
			}
			// 删除这条任务
			taskManageService.deleteTask(task);
		}else{
			return "redirect:/notlimit";

		}
		return "redirect:/taskmanage";

	}

	/**
	 * 接收人这边删除
	 */
	@RequestMapping("/myshanchu")
	public String deleteFromReceive(HttpServletRequest req, @SessionAttribute("userId") Long userId) {
		// 用户id
		// 得到任务的 id
		Long loggerTaskId = Long.parseLong(req.getParameter("id"));
		Long pkid = userService.findPkId(loggerTaskId, userId);
		taskManageService.delete(pkid);

		return "redirect:/mytask";

	}





}
