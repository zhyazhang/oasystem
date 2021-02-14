package com.aifurion.oasystem.dao.schedule;

import com.aifurion.oasystem.entity.schedule.ScheduleList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/22 9:42
 */
public interface ScheduleDao extends JpaRepository<ScheduleList, Long> {

	@Query("from ScheduleList s where s.user.userId=?1")
    List<ScheduleList> findStart(long userid);
}