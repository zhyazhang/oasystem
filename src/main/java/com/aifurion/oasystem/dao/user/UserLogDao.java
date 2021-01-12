package com.aifurion.oasystem.dao.user;

import com.aifurion.oasystem.entity.user.UserLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/12 12:30
 */

@Repository
public interface UserLogDao extends JpaRepository<UserLog, Long> {

    //查找出最新一条记录
	@Query(nativeQuery=true,value=("SELECT * from aoa_user_log where aoa_user_log.user_id=?1 ORDER BY aoa_user_log.log_time DESC LIMIT 0,1"))
	UserLog findByUserLast(long userId);

	//限制13条
	@Query(nativeQuery=true,value=("SELECT * from aoa_user_log where aoa_user_log.user_id=?1 ORDER BY aoa_user_log.log_time DESC LIMIT 0,13"))
    List<UserLog> findByUser(long userId);

	//模糊查询
	@Query("from UserLog u where u.user.userId=?1 and (u.title like %?2% or DATE_format(u.logTime,'%Y-%m-%d ') like %?2% or u.ipAddr like %?2%) order by u.logTime DESC")
    Page<UserLog> findBaseKey(long userId, String baseKey, Pageable pa);

	//按照时间降序(默认)
	@Query("from UserLog u where u.user.userId=?1 order by u.logTime DESC")
	Page<UserLog> findByUserOrderBylogTimeDesc(long userId,Pageable pa);

	//按照时间升序
	@Query("from UserLog u where u.user.userId=?1 order by u.logTime ASC")
	Page<UserLog> findByUserOrderBylogTimeAsc(long userId,Pageable pa);


}
