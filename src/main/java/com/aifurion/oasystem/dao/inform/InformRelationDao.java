package com.aifurion.oasystem.dao.inform;

import com.aifurion.oasystem.entity.notice.NoticeUserRelation;
import com.aifurion.oasystem.entity.notice.NoticesList;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 12:08
 */

@Repository
public interface InformRelationDao extends PagingAndSortingRepository<NoticeUserRelation, Long> {

	//根据用户找到通知联系人中间的集合
	List<NoticeUserRelation> findByUserId(User userId);

	//找到该用户未读的消息
	List<NoticeUserRelation> findByReadAndUserId(Boolean read, User userid);

	//根据通知找到所有的通知联系表中的集合
	List<NoticeUserRelation> findByNoticeId(NoticesList notice);

	//根据用户id和通知id找到唯一的对象
	NoticeUserRelation findByUserIdAndNoticeId(User userId,NoticesList notice);


}
