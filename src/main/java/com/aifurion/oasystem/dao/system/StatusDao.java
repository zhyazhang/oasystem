package com.aifurion.oasystem.dao.system;

import com.aifurion.oasystem.entity.system.SystemStatusList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 12:11
 */
@Repository
public interface StatusDao extends PagingAndSortingRepository<SystemStatusList, Long> {

//根据模块名和名字查找到唯一对象
	SystemStatusList findByStatusModelAndStatusName(String statusModel,String statusName);

	//根据模块名查找到状态集合
	List<SystemStatusList> findByStatusModel(String statusModel);

	List<SystemStatusList> findByStatusNameLikeOrStatusModelLike(String name,String name2);



	@Query("select sl.statusName from SystemStatusList sl where sl.statusId=:id")
	String findname(@Param("id")Long id);

	@Query("select sl.statusColor from SystemStatusList sl where sl.statusId=:id")
	String findcolor(@Param("id")Long id);



}
