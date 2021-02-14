package com.aifurion.oasystem.dao.mail;

import com.aifurion.oasystem.entity.mail.MailReceiver;
import com.aifurion.oasystem.entity.mail.Pagemail;
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
 * @date ：2021/1/13 13:58
 */

@Repository
public interface MailReceiverDao extends PagingAndSortingRepository<MailReceiver, Long> {


	//未读邮件查询
	List<MailReceiver> findByReadAndDelAndReciverId(Boolean read, Boolean del, User reciverid);

	@Query("select mr.mailId.mailId from MailReceiver mr where mr.reciverId=?1")
	List<Long> findByReciverId(User user);

	@Query("from MailReceiver mr where mr.reciverId=?1 and mr.mailId.mailId=?2")
	MailReceiver findbyReciverIdAndmailId(User user,Long id);



	//收件箱查询
	@Query("select new com.aifurion.oasystem.entity.mail.Pagemail(list.mailId,list.mailType,list.mailStatusid,list.mailTitle,list.inReceiver,list.mailFileid.attachmentId,list.mailCreateTime,mr.star,mr.read) "
			+ "from MailReceiver as mr ,Inmaillist as list where list.mailId=mr.mailId.mailId and mr.reciverId=?1 and mr.del=?2 order by list.mailCreateTime DESC")
    Page<Pagemail> findmail(User user, Boolean bo, Pageable pa);

	//邮件主题或者接收人的模糊查询
	@Query("select new com.aifurion.oasystem.entity.mail.Pagemail(list.mailId,list.mailType,list.mailStatusid,list.mailTitle,list.inReceiver,list.mailFileid.attachmentId,list.mailCreateTime,mr.star,mr.read) "
			+ "from MailReceiver as mr ,Inmaillist as list where list.mailId=mr.mailId.mailId and mr.reciverId=?1 and mr.del=?2 and (list.mailTitle like %?3% or list.inReceiver like %?3%) order by list.mailCreateTime DESC")
	Page<Pagemail> findmails(User user,Boolean bo,String title,Pageable pa);

	//根据状态查询接收邮件
	@Query("select new com.aifurion.oasystem.entity.mail.Pagemail(list.mailId,list.mailType,list.mailStatusid,list.mailTitle,list.inReceiver,list.mailFileid.attachmentId,list.mailCreateTime,mr.star,mr.read) "
			+ "from MailReceiver as mr ,Inmaillist as list where list.mailId=mr.mailId.mailId and mr.reciverId=?1 and list.mailStatusid=?2 and mr.del=?3 order by list.mailCreateTime DESC")
	Page<Pagemail> findmailbystatus(User tu,Long statusId,Boolean bo,Pageable pa);

	//根据状态查询接收邮件
	@Query("select new com.aifurion.oasystem.entity.mail.Pagemail(list.mailId,list.mailType,list.mailStatusid,list.mailTitle,list.inReceiver,list.mailFileid.attachmentId,list.mailCreateTime,mr.star,mr.read) "
			+ "from MailReceiver as mr ,Inmaillist as list where list.mailId=mr.mailId.mailId and mr.reciverId=?1 and list.mailType=?2 and mr.del=?3 order by list.mailCreateTime DESC")
	Page<Pagemail> findmailbytype(User tu,Long typeid,Boolean bo,Pageable pa);

	List<MailReceiver> findByDelAndReciverId(Boolean b,User u);

	@Query("select mr.del from MailReceiver as mr where mr.mailId.mailId=?1 ")
	List<Boolean> findbyMailId(Long id);

	List<MailReceiver> findByMailId(Long id);


}
