package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.AubUser;
import com.aifurion.oasystem.entity.process.ProcessList;
import com.aifurion.oasystem.entity.process.Reviewed;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:15
 */

@Repository
public interface ReviewedDao extends PagingAndSortingRepository<Reviewed, Long> {

	//根据审核人查找流程
	@Query("select new com.aifurion.oasystem.entity.process.AubUser(pro.processId,pro.typeNmae,pro.deeply,pro.processName,pro.userId.userName,pro.applyTime,rev.statusId) "
			+ "from ProcessList as pro,Reviewed as rev where rev.proId.processId=pro.processId and rev.userId=?1 and rev.del=?2 order by rev.statusId")
    Page<AubUser> findByUserIdOrderByStatusId(User user, Boolean bo, Pageable pa);

	//根据申请人和审核人查找流程
	@Query("select new com.aifurion.oasystem.entity.process.AubUser(pro.processId,pro.typeNmae,pro.deeply,pro.processName,pro.userId.userName,pro.applyTime,rev.statusId) "
			+ "from ProcessList as pro,Reviewed as rev where rev.proId.processId=pro.processId and rev.userId=?1 and pro.userId=?2 and rev.del=?3 order by rev.statusId")
	Page<AubUser> findprocesslist(User user,User u,Boolean bo,Pageable pa);

	//根据状态和审核人查找流程
	@Query("select new com.aifurion.oasystem.entity.process.AubUser(pro.processId,pro.typeNmae,pro.deeply,pro.processName,pro.userId.userName,pro.applyTime,rev.statusId) "
			+ "from ProcessList as pro,Reviewed as rev where rev.proId.processId=pro.processId and rev.userId=?1 and rev.statusId=?2 and rev.del=?3 order by rev.statusId")
	Page<AubUser> findbystatusprocesslist(User user,Long statusid,Boolean bo,Pageable pa);

	//根据类型名和审核人查找流程
	@Query("select new com.aifurion.oasystem.entity.process.AubUser(pro.processId,pro.typeNmae,pro.deeply,pro.processName,pro.userId.userName,pro.applyTime,rev.statusId) "
			+ "from ProcessList as pro,Reviewed as rev where rev.proId.processId=pro.processId and rev.userId=?1 and pro.typeNmae=?2 and rev.del=?3 order by rev.statusId")
	Page<AubUser> findbytypenameprocesslist(User user,String typename,Boolean bo,Pageable pa);

	//根据标题和审核人查找流程
	@Query("select new com.aifurion.oasystem.entity.process.AubUser(pro.processId,pro.typeNmae,pro.deeply,pro.processName,pro.userId.userName,pro.applyTime,rev.statusId) "
			+ "from ProcessList as pro,Reviewed as rev where rev.proId.processId=pro.processId and rev.userId=?1 and pro.processName like %?2% and rev.del=?3 order by rev.statusId")
	Page<AubUser> findbyprocessnameprocesslist(User user,String processname,Boolean bo,Pageable pa);

	List<Reviewed> findByReviewedTimeNotNullAndProId(ProcessList pro);

	@Query(" select re from Reviewed as re where re.proId.processId=?1 and re.userId=?2")
	Reviewed findByProIdAndUserId(Long pro,User u);

}