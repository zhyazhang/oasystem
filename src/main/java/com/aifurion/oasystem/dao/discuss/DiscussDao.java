package com.aifurion.oasystem.dao.discuss;

import com.aifurion.oasystem.entity.discuss.Discuss;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 15:15
 */
public interface DiscussDao extends JpaRepository<Discuss, Long> {


	//根据用户来查找讨论区的自己所发布的；
	Page<Discuss> findByUser(User user, Pageable pa);

	//根据用户的标题来找
	Page<Discuss> findByUserAndTitleLike(User user, String title, Pageable pa);

	//根据标题来找
	Page<Discuss> findByTitleLike(String title,Pageable pa);
}
