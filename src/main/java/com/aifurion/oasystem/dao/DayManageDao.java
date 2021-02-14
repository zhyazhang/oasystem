package com.aifurion.oasystem.dao;

import com.aifurion.oasystem.entity.schedule.ScheduleList;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 11:43
 */


@Repository
public interface DayManageDao extends JpaRepository<ScheduleList, Long> {

    List<ScheduleList> findByUser(User user);

	List<ScheduleList> findByUsersIn(List<User> users);

	Page<ScheduleList> findByUsersIn(List<User> users, Pageable pa);

	Page<ScheduleList> findByUser(User user,Pageable pa);

	Page<ScheduleList> findByUserAndUsersIn(User user, List<User> users, Pageable pa);


}
