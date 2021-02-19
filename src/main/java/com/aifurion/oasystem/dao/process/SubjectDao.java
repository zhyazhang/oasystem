package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.Subject;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:14
 */
@Repository
public interface SubjectDao extends PagingAndSortingRepository<Subject, Long> {

	List<Subject> findByParentId(Long id);

	List<Subject> findByParentIdNot(Long id);


}

