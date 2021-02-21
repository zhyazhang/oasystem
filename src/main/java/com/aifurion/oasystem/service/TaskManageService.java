package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.task.Tasklist;
import com.aifurion.oasystem.entity.task.Tasklogger;
import com.aifurion.oasystem.entity.task.Taskuser;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/21 11:29
 */
public interface TaskManageService {

    Tasklist saveTasks(Tasklist tasklist);

    void saveTaskUser(Taskuser taskuser);

    void saveTaskLogger(Tasklogger tasklogger);


    int updateStatusId(Long taskId, Integer statusId);


    int updateUserStatusId(Long taskId, Integer statusId);

    int delete(Long id);


    void deleteTask(Tasklist task);


    int deleteLogger(Long taskId);


    Page<Tasklist> getTaskListByRelease(int page, int size, String val, User user);


    List<Map<String, Object>> getReleaseInfo(Page<Tasklist> taskList, User user);


    Page<Tasklist> getTaskListByReceive(Long userId, String title, int page, int size);


    List<Map<String, Object>> getReceiveInfo(Page<Tasklist> tasklist, Long userid);

    Tasklist findOne(Long id);

    List<Tasklogger> findTaskLoggerByTaskId(Long id);


    Page<Tasklist> findByTickingIsNotNull(Pageable pageable);


    Long findStatusByUserIdAndTaskId(Long userId, Long loggerTaskId);


    List<Integer> findTaskStatusByTaskId(Long id);















}
