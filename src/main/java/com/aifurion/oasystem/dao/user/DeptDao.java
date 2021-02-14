package com.aifurion.oasystem.dao.user;

import com.aifurion.oasystem.entity.user.Dept;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 14:24
 */
public interface DeptDao extends PagingAndSortingRepository<Dept, Long> {

	List<Dept> findByDeptId(Long id);


	@Query("select de.deptName from Dept de where de.deptId=:id")
	String findname(@Param("id")Long id);
}
