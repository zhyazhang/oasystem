package com.aifurion.oasystem.dao.user;

import com.aifurion.oasystem.entity.user.Position;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 14:17
 */
public interface PositionDao extends PagingAndSortingRepository<Position, Long> {

	@Query("select po.name from Position po where po.id=:id")
	String findNameById(@Param("id")Long id);

	List<Position> findByDeptidAndNameNotLike(Long deptid, String name);

	List<Position> findByDeptidAndNameLike(Long deptid,String name);

	List<Position> findByDeptid(Long deletedeptid);
}
