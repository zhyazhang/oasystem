package com.aifurion.oasystem.dao.task;

import com.aifurion.oasystem.entity.task.Tasklogger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/21 11:31
 */
public interface TaskloggerDao extends PagingAndSortingRepository<Tasklogger, Long> {

	@Query("select tl from Tasklogger tl where tl.taskId.taskId=:id")
    List<Tasklogger> findByTaskId(@Param("id")Long id);
}
