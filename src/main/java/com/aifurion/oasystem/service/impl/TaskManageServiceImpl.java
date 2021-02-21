package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.system.StatusDao;
import com.aifurion.oasystem.dao.system.TypeDao;
import com.aifurion.oasystem.dao.task.TaskDao;
import com.aifurion.oasystem.dao.task.TaskUserDao;
import com.aifurion.oasystem.dao.task.TaskloggerDao;
import com.aifurion.oasystem.dao.user.DeptDao;
import com.aifurion.oasystem.dao.user.UserDao;
import com.aifurion.oasystem.entity.system.SystemStatusList;
import com.aifurion.oasystem.entity.system.SystemTypeList;
import com.aifurion.oasystem.entity.task.Tasklist;
import com.aifurion.oasystem.entity.task.Tasklogger;
import com.aifurion.oasystem.entity.task.Taskuser;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.TaskManageService;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/21 11:29
 */

@Service
public class TaskManageServiceImpl implements TaskManageService {


    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskUserDao taskUserDao;
    @Autowired
    private TaskloggerDao taskloggerDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private StatusDao statusDao;
    @Autowired
    private TypeDao typeDao;
    @Autowired
    private DeptDao deptDao;


    @Override
    public List<Integer> findTaskStatusByTaskId(Long id) {
        return taskUserDao.findByTaskId(id);
    }

    @Override
    public Long findStatusByUserIdAndTaskId(Long userId, Long loggerTaskId) {


        return taskUserDao.findByUserIdAndTaskId(userId, loggerTaskId);
    }

    @Override
    public Page<Tasklist> findByTickingIsNotNull(Pageable pageable) {
        return taskDao.findByTickingIsNotNull(pageable);
    }

    @Override
    public void saveTaskLogger(Tasklogger tasklogger) {

        taskloggerDao.save(tasklogger);
    }

    @Override
    public List<Tasklogger> findTaskLoggerByTaskId(Long id) {
        return taskloggerDao.findByTaskId(id);
    }

    @Override
    public void saveTaskUser(Taskuser taskuser) {

        taskUserDao.save(taskuser);
    }

    @Override
    public Tasklist findOne(Long id) {
        return taskDao.findById(id).get();
    }

    @Override
    public Tasklist saveTasks(Tasklist tasklist) {

        return taskDao.save(tasklist);
    }


    //修改任务表里面的状态
    @Override
    public int updateStatusId(Long taskId, Integer statusId) {
        return taskDao.update(taskId, statusId);
    }


    //修改任务表中间表的任务状态
    @Override
    public int updateUserStatusId(Long taskId, Integer statusId) {


        return taskUserDao.updatestatus(taskId, statusId);
    }


    //删除任务中间表
    @Override
    public int delete(Long id) {
        int i = 0;
        if (!Objects.isNull(id)) {
            taskUserDao.deleteById(id);
            i = 1;
        }
        return i;
    }

    //删除任务

    @Override
    public void deleteTask(Tasklist task) {

        taskDao.delete(task);

    }


    //删除日志表
    @Override
    public int deleteLogger(Long taskId) {

        int i = 0;
        List<Tasklogger> taskLogger = taskloggerDao.findByTaskId(taskId);
        if (taskLogger.size() != 0) {
            for (Tasklogger logger : taskLogger) {
                taskloggerDao.delete(logger);
            }
            i = 1;
        }
        return i;
    }

    @Override
    public Page<Tasklist> getTaskListByRelease(int page, int size, String val, User user) {
        Page<Tasklist> tasklist = null;
        List<Sort.Order> orders = new ArrayList<>();
        Pageable pa = PageRequest.of(page, size);
        if (StringUtil.isEmpty(val)) {
            // 根据发布人id查询任务
            orders.addAll(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "top"), new Sort.Order(Sort.Direction.DESC, "modifyTime")));
            Sort sort = Sort.by(orders);
            pa = PageRequest.of(page, size, sort);
            tasklist = taskDao.findByUsersId(user, pa);

        } else if (("类型").equals(val)) {
            tasklist = taskDao.findByUsersIdOrderByTypeId(user, pa);
        } else if (("状态").equals(val)) {
            orders.addAll(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "cancel"), new Sort.Order(Sort.Direction.ASC, "statusId")));
            Sort sort = Sort.by(orders);
            pa = PageRequest.of(page, size, sort);
            tasklist = taskDao.findByUsersId(user, pa);
        } else if (("发布时间").equals(val)) {
            tasklist = taskDao.findByUsersIdOrderByPublishTimeDesc(user, pa);
        } else {
            tasklist = taskDao.findByTitleLikeAndUsersId(val, user, pa);
        }
        return tasklist;

    }

    @Override
    public List<Map<String, Object>> getReleaseInfo(Page<Tasklist> taskList, User user) {
        String username = user.getUserName();
        String deptname = deptDao.findname(user.getDept().getDeptId());
        List<Map<String, Object>> list = new ArrayList<>();
        List<Tasklist> task = taskList.getContent();
        for (Tasklist tasklist : task) {
            Map<String, Object> result = new HashMap<>();
            Long statusid = tasklist.getStatusId().longValue();
            result.put("taskid", tasklist.getTaskId());
            result.put("typename", typeDao.findname(tasklist.getTypeId()));
            result.put("statusname", statusDao.findname(statusid));
            result.put("statuscolor", statusDao.findcolor(statusid));
            result.put("title", tasklist.getTitle());
            result.put("publishtime", tasklist.getPublishTime());
            result.put("zhiding", tasklist.getTop());
            result.put("cancel", tasklist.getCancel());
            result.put("username", username);
            result.put("deptname", deptname);
            list.add(result);
        }
        return list;
    }

    @Override
    public Page<Tasklist> getTaskListByReceive(Long userId, String title, int page, int size) {
        Pageable pa = PageRequest.of(page, size);
        Page<Tasklist> tasklist = null;
        // 根据接收人id查询任务id
        List<Long> taskid = taskUserDao.findByUserId(userId);
        // 类型
        SystemTypeList type = typeDao.findByTypeModelAndTypeName("aoa_task_list", title);
        // 状态
        SystemStatusList status = statusDao.findByStatusModelAndStatusName("aoa_task_list", title);
        // 找用户
        User user = userDao.findByUserName(title);

        if (StringUtil.isEmpty(title)) {
            List<Sort.Order> orders = new ArrayList<>(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "cancel"), new Sort.Order(Sort.Direction.ASC, "statusId")));
            Sort sort = Sort.by(orders);
            pa = PageRequest.of(page, size, sort);
            if (taskid.size() > 0) {

                tasklist = taskDao.findTaskByTaskIds(taskid, pa);
            }
        } else if (!Objects.isNull(type)) {

            tasklist = taskDao.findtaskTypeIdAndTaskId(type.getTypeId(), taskid, pa);

        } else if (!Objects.isNull(status)) {
            // Long转换成Integer
            Integer statusid = Integer.parseInt(status.getStatusId().toString());
            // 根据找出的taskid和状态id查找任务
            tasklist = taskDao.findtaskStatusIdAndCancelAndTaskId(statusid, taskid, pa);

        } else if (("已取消").equals(title)) {
            tasklist = taskDao.findtaskCancelAndTaskId(true, taskid, pa);

        } else if (!Objects.isNull(user)) {

            tasklist = taskDao.findtaskUsersIdAndTaskId(user, taskid, pa);

        } else {
            // 根据title和taskid进行模糊查询
            tasklist = taskDao.findtaskByTitleLikeAndTaskId(taskid, title, pa);


        }

        return tasklist;
    }

    @Override
    public List<Map<String, Object>> getReceiveInfo(Page<Tasklist> tasklist, Long userid) {
       List<Map<String, Object>> list = new ArrayList<>();
        if (tasklist != null) {

            List<Tasklist> task = tasklist.getContent();

            for (int i = 0; i < task.size(); i++) {
                Map<String, Object> result = new HashMap<>();
                // 查询任务id
                Long tid = task.get(i).getTaskId();

                // 查询接收人的任务状态id
                Long statusid = taskUserDao.findByUserIdAndTaskId(userid, tid);

                // 查询发布人
                User user = userDao.findById(task.get(i).getUsersId().getUserId()).get();
                String username = user.getUserName();
                String deptname = deptDao.findname(user.getDept().getDeptId());

                result.put("taskid", tid);
                result.put("typename", typeDao.findname(task.get(i).getTypeId()));
                result.put("statusname", statusDao.findname(statusid));
                result.put("statuscolor", statusDao.findcolor(statusid));
                result.put("title", task.get(i).getTitle());
                result.put("publishtime", task.get(i).getPublishTime());
                result.put("zhiding", task.get(i).getTop());
                result.put("cancel", task.get(i).getCancel());
                result.put("username", username);
                result.put("deptname", deptname);

                list.add(result);
            }
        }

        return list;
    }
}
